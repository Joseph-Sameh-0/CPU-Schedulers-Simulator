package org.example.data;

public class Process implements Runnable {

  protected final String name;
  protected int executedTime = 0;
  protected int burstTime;
  protected final int arrivalTime;
  protected final int priority;
  protected int quantum;
  protected Schedular schedular;
  protected Boolean running = false;

  public Process(
    String name,
    int burstTime,
    int arrivalTime,
    int priority,
    int quantum,
    Schedular schedular
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

  public int getExecutedTime() {
    return executedTime;
  }

  public void setExecutedTime(int executedTime) {
    this.executedTime = executedTime;
  }

  public int getBurstTime() {
    return burstTime;
  }

  public void setBurstTime(int burstTime) {
    this.burstTime = burstTime;
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

  @Override
  public void run() {
    try {
      Thread.sleep(arrivalTime * 1000L);
      System.out.println("process " + name + " arrived");
      schedular.process(this);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void execute() {
    System.out.println("process " + name + " executes");
  }

  public void interrupt() {
    System.out.println("process " + name + " interrupted");
  }
}
