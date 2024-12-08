package org.example.FCAI;

public class FCAIProcess implements Comparable<FCAIProcess>, Runnable {

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
        "New Process " +
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
              "Process " +
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
            running = true;
            // System.out.print(
            //   "FCAIProcess " + name + " quantum updated from " + quantum
            // );
            FCAICalc.updateQuantum(this);
            System.out.println(" to " + quantum);
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
