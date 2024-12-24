package org.example.SRTF;

import java.awt.Color;

public class SRTFProcess implements Comparable<SRTFProcess>, Runnable {

    private final int number;
    protected final String name;
    protected int executedTime = 0;
    protected int burstTime;
    protected final int arrivalTime;
    protected SRTFScheduler scheduler;
    protected Boolean running = false;
    private int remainingTime;
    private int waitingTime = 0;
    private int effectiveRemainingTime;
    double agingFactor = 1;
    private final Color color;

    public SRTFProcess(
            int number,
            String name,
            int burstTime,
            int arrivalTime,
            SRTFScheduler scheduler,
            Color color
    ) {
        this.number = number;
        this.name = name;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.remainingTime = burstTime;
        this.effectiveRemainingTime = burstTime;
        this.scheduler = scheduler;
        this.color = color;
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

    public int getNumber() {
        return number;
    }

    public Color getColor() {
        return color;
    }


    @Override
    public void run() {
        try {
            Thread.sleep((arrivalTime * 1000));
            // System.out.println("Process " + name + " arrived");
            System.out.println("process " + name + " pinged the scheduler");
            new Thread(()->{
                scheduler.process(this);
            }).start();

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
        scheduler.highlightProcessRow(number, color);
        // create a new thread for the try
        try {
            running = true;
            while (running && remainingTime > 0) {
                Thread.sleep(1000L);
                if (running) {
                    remainingTime--;
                    effectiveRemainingTime = (int) Math.floor(this.getRemainingTime() - (this.getWaitingTime() / agingFactor));
                    if (remainingTime == 0) {
                        break;
                        
                    }
                    System.out.println(
                            "Process " +
                                    name +
                                    " running -> remainingTime:" +
                                    remainingTime +
                                    " burstTime: " +
                                    burstTime
                    );
                    scheduler.highlightProcessRow(number, color);
                    
                }
            }


            if (remainingTime == 0) {
                System.out.println("Process " + name + " finished");
                running = false;
                scheduler.processFinished(this);
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
                System.out.println(
                        "Process " +
                                name +
                                " waiting -> effectiveRemainingTime:" +
                                effectiveRemainingTime +
                                " remaining time"+
                                remainingTime +
                                " burstTime: " +
                                burstTime
                );

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
