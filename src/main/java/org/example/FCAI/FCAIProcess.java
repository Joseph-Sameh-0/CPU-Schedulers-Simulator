package org.example.FCAI;

import org.example.data.Process;
import org.example.data.Schedular;

public class FCAIProcess extends Process {

  private int remainingQuantum;

  public FCAIProcess(
    String name,
    int burstTime,
    int arrivalTime,
    int priority,
    int quantum,
    Schedular schedular
  ) {
    super(name, burstTime, arrivalTime, priority, quantum, schedular);
  }

  public int getRemainingQuantum() {
    return remainingQuantum;
  }

  public void setRemainingQuantum(int remainingQuantum) {
    this.remainingQuantum = remainingQuantum;
  }

  @Override
  public void execute() {
    System.out.println("FCAIprocess " + name + " executes");
    try {
      remainingQuantum = quantum;
      running = true;
      while (running && burstTime > 0 && remainingQuantum > 0) {
        remainingQuantum--;
        burstTime--;
        System.out.println("FCAIprocess " + name + " running -> remainingQuantum:"+ remainingQuantum+" burstTime: " + burstTime);
        Thread.sleep(1000L);
        schedular.signal();
      }
      running = false;
      if (burstTime > 0) {
        System.out.println("burstTime > 0: FCAIprocess " + name + " processed again");
        schedular.process(this);
      } else {
        System.out.println(name + " Completed");
        schedular.signal();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public void interrupt() {
    System.out.println("FCAIprocess " + name + " interrupted");
    running = false;
    System.out.print("FCAIprocess quantum updated from " + quantum);
    FCAICalc.updateQuantum(this);
    System.out.println(" to " + quantum);
  }
}
