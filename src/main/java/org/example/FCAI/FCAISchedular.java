package org.example.FCAI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CustomQueue<T extends Comparable<T>> {

  private final List<T> list;

  public CustomQueue() {
    this.list = Collections.synchronizedList(new ArrayList<>());
  }

  // Add an element to the list
  public void add(T element) {
    list.add(element);
  }

  // Get the "first in" element
  public T getFirstIn() {
    synchronized (list) {
      return list.isEmpty() ? null : list.get(0);
    }
  }

  // Get the smallest element
  public T getSmallest() {
    synchronized (list) {
      if (list.isEmpty()) {
        return null;
      }
      T smallest = list.get(0);
      for (T element : list) {
        if (element.compareTo(smallest) < 0) {
          smallest = element;
        }
      }
      return smallest;
    }
  }

  // Remove the "first in" element
  public T removeFirstIn() {
    synchronized (list) {
      return list.isEmpty() ? null : list.remove(0);
    }
  }

  // Remove the smallest element
  public T removeSmallest() {
    synchronized (list) {
      if (list.isEmpty()) {
        return null;
      }
      T smallest = getSmallest();
      list.remove(smallest);
      return smallest;
    }
  }

  // Check if the queue is empty
  public boolean isEmpty() {
    synchronized (list) {
      return list.isEmpty();
    }
  }
}

class FCAICalc {

  final double v1;
  final double v2;

  FCAICalc(int lastArrivalTime, int maxBurstTime) {
    this.v1 = (double) lastArrivalTime / 10;
    this.v2 = (double) maxBurstTime / 10;
  }

  int calcFactor(FCAIProcess p) {
    double factor =
      (10 - p.getPriority()) +
      Math.ceil((p.getArrivalTime() / v1)) +
      Math.ceil((p.getBurstTime() / v2));

    return ((int) Math.ceil(factor));
  }

  public static void updateQuantum(FCAIProcess p) {
    if (p.getRemainingQuantum() == 0) {
      p.setQuantum(p.getQuantum() + 2);
    } else {
      p.setQuantum(p.getQuantum() + p.getRemainingQuantum());
    }
  }
}

class FCAIProcess implements Comparable<FCAIProcess>, Runnable {

  protected final String name;
  protected int executedTime = 0;
  protected int burstTime;
  protected final int arrivalTime;
  protected final int priority;
  protected int quantum;
  protected FCAISchedular schedular;
  protected Boolean running = false;
  private int remainingQuantum;
  private FCAICalc calc;

  private static enum Status {
    NotArrived,
    Arrived,
  }

  private Status status = Status.NotArrived;

  public FCAIProcess(
    String name,
    int burstTime,
    int arrivalTime,
    int priority,
    int quantum,
    FCAISchedular schedular
  ) {
    this.name = name;
    this.burstTime = burstTime;
    this.arrivalTime = arrivalTime;
    this.priority = priority;
    this.quantum = quantum;
    this.schedular = schedular;
  }

  public String getName() {
    return name;
  }

  public int getBurstTime() {
    return burstTime;
  }

  public int getArrivalTime() {
    return arrivalTime;
  }

  public int getPriority() {
    return priority;
  }

  public int getQuantum() {
    return quantum;
  }

  public void setQuantum(int quantum) {
    this.quantum = quantum;
  }

  public int getRemainingQuantum() {
    return remainingQuantum;
  }

  public void setCalc(FCAICalc calc) {
    this.calc = calc;
  }

  public int getFactor() {
    return calc.calcFactor(this);
  }

  @Override
  public void run() {
    if (status == Status.NotArrived) {
      status = Status.Arrived;
      try {
        Thread.sleep(arrivalTime * 1000L);
        // System.out.println("Process " + name + " arrived");
        new Thread(() -> {
          schedular.process(this);
        })
          .start();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    } else {
      // System.out.println("FCAIProcess " + name + " executes");
      System.out.println(
        "FCAIProcess " +
        name +
        " running -> remainingQuantum:" +
        quantum +
        " burstTime: " +
        burstTime
      );

      try {
        remainingQuantum = quantum;
        running = true;
        while (running && burstTime > 0 && remainingQuantum > 0) {
          Thread.sleep(1000L);

          if (running) {
            burstTime--;
            remainingQuantum--;

            System.out.println(
              "FCAIProcess " +
              name +
              " running -> remainingQuantum:" +
              remainingQuantum +
              " burstTime: " +
              burstTime
            );
            // System.out.println(
            //   "FCAIProcess " +
            //   name +
            //   " running -> remainingQuantum:" +
            //   remainingQuantum +
            //   " burstTime: " +
            //   burstTime
            // );

            if (running && isPreemptive()) {
              boolean signal = schedular.signal();
              if (signal) {
                if (burstTime > 0) {
                  running = false;
                  // System.out.println("Process " + name + " preempted");
                  // System.out.print(
                  //   "FCAIProcess " + name + " quantum updated from " + quantum
                  // );
                  FCAICalc.updateQuantum(this);
                  // System.out.println(" to " + quantum);
                  return;
                } else if (burstTime == 0) {
                  running = false;
                  // System.out.println("FCAIProcess " + name + " finished");
                  return;
                }
              }
            }
          }
        }

        boolean signal = schedular.signal();
        if (signal) {
          if (burstTime == 0) {
            running = false;
            // System.out.println("FCAIProcess " + name + " finished");
            return;
          } else {
            running = false;
            // System.out.println("FCAIProcess " + name + " preempted");
            // System.out.print(
            // "FCAIProcess " + name + " quantum updated from " + quantum
            // );
            FCAICalc.updateQuantum(this);
            // System.out.println(" to " + quantum);
            return;
          }
        } else {
          if (burstTime > 0) {
            running = true;
            // System.out.print(
            //   "FCAIProcess " + name + " quantum updated from " + quantum
            // );
            FCAICalc.updateQuantum(this);
            // System.out.println(" to " + quantum);
            //run again
            run();
            return;
          } else {
            running = false;
            // System.out.println("FCAIProcess " + name + " finished");
            return;
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }

  public Boolean isPreemptive() {
    double preemptiveFactor = (quantum - remainingQuantum) / (double) quantum;
    // System.out.println("preemptiveFactor = " + preemptiveFactor);
    if (preemptiveFactor >= 0.4) {
      // System.out.println(p.getName() + " is Preemptive");
      return true;
    }
    // System.out.println(p.getName() + " is not Preemptive");
    return false;
  }

  @Override
  public int compareTo(FCAIProcess other) {
    return Integer.compare(calc.calcFactor(this), calc.calcFactor(other));
  }
}

public class FCAISchedular implements Runnable {

  FCAICalc calc;
  FCAIProcess runningProcess;
  private final CustomQueue<FCAIProcess> waitingQueue;
  protected List<FCAIProcess> processes;
  private int finishedProcesses;

  public FCAISchedular() {
    this.waitingQueue = new CustomQueue<>();
  }

  @Override
  public void run() {
    new Thread(() -> {
      int g = 0;
      while (finishedProcesses < processes.size()) {
        try {
          Thread.sleep(500);
          System.out.println(
            "------------------------------------------------------------------------- time " +
            g +
            " -> " +
            (g + 1)
          );
          g++;
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    })
      .start();

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
          System.out.println(
            "FCAIprocess " + runningProcess.getName() + " finished the quantum"
          );
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
          System.out.println(
            "FCAIprocess " + runningProcess.getName() + " finished"
          );
          finishedProcesses++;
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
          System.out.println(
            "FCAI process " + runningProcess.getName() + " interrupted"
          );
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
        System.out.println(
          "FCAIprocess " + runningProcess.getName() + " finished"
        );
        finishedProcesses++;
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
