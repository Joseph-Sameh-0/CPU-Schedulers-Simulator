package org.example.FCAI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FCAISchedular implements Runnable {

  FCAICalc calc;
  FCAIProcess runningProcess;
  private final CustomQueue<FCAIProcess> waitingQueue;
  protected List<FCAIProcess> processes;

  public FCAISchedular() {
    this.waitingQueue = new CustomQueue<>();
  }

  @Override
  public void run() {
    for (FCAIProcess p : processes) {
      new Thread(p).start();
    }
  }

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
      for (FCAIProcess p : processes) {
        p.setCalc(calc);
      }
    } catch (IOException e) {
      System.out.println("Error reading the file: " + e.getMessage());
    }
  }

  public void process(FCAIProcess process) {
    // System.out.println(
    //   "FCAIScheduler process the " + process.getName() + " process"
    // );

    if (runningProcess == null) {
      // System.out.println("No process is running");

      runningProcess = process;
      new Thread(runningProcess).start();
      return;
    }

    waitingQueue.add(process);
  }

  public boolean signal() {
    // System.out.println("signal from FCAI scheduler");

    if (runningProcess != null) {
      // System.out.println("runningProcess != null");
      if (!waitingQueue.isEmpty()) {
        // System.out.println("waitingQueue != empty");

        // if the current process is finished the quantum
        // then remove it from the running process
        // and execute the next process in the waiting queue

        if (runningProcess.getRemainingQuantum() == 0) {
          // System.out.println(
          //   "process " + runningProcess.getName() + " finished the quantum"
          // );
          // runningProcess.interrupt();
          waitingQueue.add(runningProcess);
          runningProcess = waitingQueue.removeFirstIn();
          new Thread(runningProcess).start();

          return true;
        }

        // if the current process is finished
        // then remove it from the running process
        // and execute the next process in the waiting queue
        if (runningProcess.getBurstTime() == 0) {
          // System.out.println(
          //   "process " + runningProcess.getName() + " finished"
          // );
          // runningProcess.interrupt();
          runningProcess = waitingQueue.removeFirstIn();
          new Thread(runningProcess).start();
          return true;
        }

        // if there is a process has a smaller factor than the running process
        // then interrupt the current process
        // and execute the process with smaller factor

        int smallestFactor = waitingQueue.getSmallest().getFactor();
        int currentFactor = runningProcess.getFactor();

        if (runningProcess.isPreemptive() && smallestFactor <= currentFactor) {
          // System.out.println(
          //   "process " +
          //   waitingQueue.getSmallest().getName() +
          //   " has smaller factor " +
          //   smallestFactor +
          //   " than " +
          //   runningProcess.getName() +
          //   " " +
          //   currentFactor
          // );
          waitingQueue.add(runningProcess);
          // runningProcess.interrupt();
          runningProcess = waitingQueue.removeSmallest();
          new Thread(runningProcess).start();
          return true;
        } else {
          // System.out.println(
          //   "no process has smaller factor than " + runningProcess.getName()
          // );
          return false;
        }
      } else if (
        runningProcess.getRemainingQuantum() == 0 &&
        runningProcess.getBurstTime() != 0
      ) {
        // System.out.println(" the only process is continued ");
        // runningProcess.interrupt();
        // runningProcess.execute();
        return false;
      } else if (runningProcess.getBurstTime() == 0) {
        // System.out.println(" there is no process to execute ");
        runningProcess = null;
        return true;
      } else {
        // System.out.println("no change");
        return false;
      }
    }
    if (!waitingQueue.isEmpty()) {
      // System.out.println(
      //   "waiting queue is not empty and the running process is null"
      // );
      runningProcess = waitingQueue.removeFirstIn();
      new Thread(runningProcess).start();
      return true;
    }
    return false;
  }
}
