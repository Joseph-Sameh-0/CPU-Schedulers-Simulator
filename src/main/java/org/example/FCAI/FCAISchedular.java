package org.example.FCAI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.example.data.Process;
import org.example.data.Schedular;

public class FCAISchedular extends Schedular {

  FCAICalc calc;
  Process runningProcess;
  private final BlockingQueue<Process> waitingQueue;

  public FCAISchedular() {
    this.waitingQueue = new LinkedBlockingQueue<>();
  }

  // @Override
  // public void setProcesses(List<Process> processes) {
  //   super.setProcesses(processes);
  //   int lastArrivalTime = 0;
  //   int maxBurstTime = 0;
  //   for (Process p : processes) {
  //     if (p.getBurstTime() > maxBurstTime) {
  //       maxBurstTime = p.getBurstTime();
  //     }
  //     if (p.getArrivalTime() > lastArrivalTime) {
  //       lastArrivalTime = p.getArrivalTime();
  //     }
  //   }
  //   calc = new FCAICalc(lastArrivalTime, maxBurstTime);
  // }

  @Override
  public void run() {
    System.out.println("FCAIScheduler running");
    for (Process p : processes) {
      new Thread(p).start();
    }
  }

  @Override
  public void setProcesses(String filename) {
    processes = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      int lastArrivalTime = 0;
      int maxBurstTime = 0;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(" ");
        String processName = parts[0];
        int burstTime = Integer.parseInt(parts[1]);
        int arrivalTime = Integer.parseInt(parts[2]);
        int priority = Integer.parseInt(parts[3]);
        int quantum = Integer.parseInt(parts[4]);

        processes.add(
          new FCAIProcess(
            processName,
            burstTime,
            arrivalTime,
            priority,
            quantum,
            this
          )
        );

        if (burstTime > maxBurstTime) {
          maxBurstTime = burstTime;
        }
        if (arrivalTime > lastArrivalTime) {
          lastArrivalTime = arrivalTime;
        }
      }
      calc = new FCAICalc(lastArrivalTime, maxBurstTime);
    } catch (IOException e) {
      System.out.println("Error reading the file: " + e.getMessage());
    }
  }

  @Override
  public void process(Process process) {
    System.out.println(
      "FCAIScheduler process the " + process.getName() + " process"
    );

    if (runningProcess == null) {
      runningProcess = process;
      runningProcess.execute();
      System.out.println(
        "FCAIScheduler executes the " + runningProcess.getName() + " process"
      );
    }

    if (process == runningProcess) {
      runningProcess.interrupt();
      waitingQueue.add(runningProcess);
      System.out.println(runningProcess.getName() + " added to waiting queue");
      runningProcess = waitingQueue.poll();
      System.out.println("runningProcess = " + runningProcess.getName());
      runningProcess.execute();
    } else if (
      FCAICalc.isPreemptive((FCAIProcess) runningProcess) &&
      (calc.calcFactor(process) < calc.calcFactor(runningProcess))
    ) {
      // System.out.println(runningProcess.getName() + " is Preemptive");
      runningProcess.interrupt();
      waitingQueue.add(runningProcess);
      System.out.println(runningProcess.getName() + " added to waitingQueue");
      runningProcess = process;
      System.out.println("runningProcess = " + process.getName());
      runningProcess.execute();
    } else {
      // System.out.println(runningProcess.getName() + " is not Preemptive");
      waitingQueue.add(process);
      System.out.println(process.getName() + " added to waitingQueue");
    }
  }

  @Override
  public void signal() {
    System.out.println("signal from FCAI scheduler");
    if (!waitingQueue.isEmpty()) {
      if (runningProcess.getBurstTime() == 0 ||
        (
          FCAICalc.isPreemptive((FCAIProcess) runningProcess) &&
          (
            calc.calcFactor(waitingQueue.peek()) <
            calc.calcFactor(runningProcess)
          )
        )
      ) {
        runningProcess.interrupt();
        runningProcess = waitingQueue.poll();
        System.out.println(
          "FCAIScheduler executes the " + runningProcess.getName() + " process"
        );
        runningProcess.execute();
      }
    }
  }
}
