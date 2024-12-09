package org.example.SRTF;

public class SRTFProcess implements Comparable<SRTFProcess>, Runnable {

  protected final String name;
  protected int executedTime = 0;
  protected int burstTime;
  protected final int arrivalTime;
  protected SRTFSchedular schedular;
  protected Boolean running = false;
  private int remainingTime;
  private int waitingTime = 0;
  private int effectiveRemainingTime = 0;
  double agingFactor = 0.8;

  public SRTFProcess(
    String name,
    int burstTime,
    int arrivalTime,
    SRTFSchedular schedular
  ) {
    this.name = name;
    this.burstTime = burstTime;
    this.arrivalTime = arrivalTime;
    this.remainingTime = burstTime;
    this.schedular = schedular;
  }

  public String getName() {
    return name;
  }

  public int getExecutedTime() {
    return executedTime;
  }
  
  public int getWaitingTime() {
    return waitingTime;
  }
  public int getEffectiveRemainingTime() {
    return effectiveRemainingTime;
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

  public int getRemainingTime() {
    return remainingTime;
  }

  public void setRemainingTime(int remainingTime) {
    this.remainingTime = remainingTime;
  }



  @Override
  public void run() {
    try {
      Thread.sleep(arrivalTime * 1000L);
      // System.out.println("Process " + name + " arrived");
      System.out.println("process " + name + " pinged the scheduler");
      schedular.process(this);
      
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public void execute() {
    // System.out.println("SRTFProcess " + name + " executes");
    System.out.println(
      "New Process " +
      name +
      " running -> remainingTime:" +
      remainingTime +
      " burstTime: " +
      burstTime
    );
    try {
      running = true;
      while (running && remainingTime > 0) {
        Thread.sleep(1000L);
        if (running) {
          remainingTime--;
          effectiveRemainingTime = (int) Math.floor(this.getRemainingTime() - (this.getWaitingTime() / agingFactor));
          System.out.println(
            "Process " +
            name +
            " running -> remainingTime:" +
            remainingTime +
            " burstTime: " +
            burstTime
          );
        }
      }
        

      if (remainingTime == 0) {
        System.out.println("Process " + name + " finished");
        running = false;
        schedular.processFinished(this);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
  public void startwaiting() {
    running = false;
    while (running == false) {
      try {
        Thread.sleep(1000L);
        waitingTime++;
        effectiveRemainingTime = (int) Math.floor(this.getRemainingTime() - (this.getWaitingTime() / agingFactor));

      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      
    }
  }
  @Override
  public int compareTo(SRTFProcess other) {
      return Integer.compare(this.getEffectiveRemainingTime(), other.getEffectiveRemainingTime());
  }
}
