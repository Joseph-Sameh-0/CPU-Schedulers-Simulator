package org.example.FCAI;

import java.awt.Color;

class FCAIProcess implements Comparable<FCAIProcess>, Runnable {

    private final int number;
    private final String name;
    private int burstTime;
    private final int arrivalTime;
    private final int priority;
    private int quantum;
    private final FCAISchedular scheduler;
    private Boolean running = false;
    private int remainingQuantum;
    private FCAICalc calc;
    private final Color color;
    private final long unitOfTime;

    public Color getColor() {
        return color;
    }

    private static enum Status {
        NotArrived,
        Arrived,
    }

    private Status status = Status.NotArrived;

    public FCAIProcess(
            int number,
            String name,
            int burstTime,
            int arrivalTime,
            int priority,
            int quantum,
            Color color,
            long unitOfTime,
            FCAISchedular schedular
    ) {
        this.number = number;
        this.name = name;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.quantum = quantum;
        this.color = color;
        this.unitOfTime = unitOfTime;
        this.scheduler = schedular;
    }

    public int getNumber() {
        return number;
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
                Thread.sleep(arrivalTime * unitOfTime);
                // System.out.println("Process " + name + " arrived");
                new Thread(() -> {
                    scheduler.process(this);
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
                            burstTime +
                            " FCAI Factor: " + calc.calcFactor(this)
            );
            scheduler.highlightProcessRow(number, color);

            try {
                remainingQuantum = quantum;
                running = true;
                while (running && burstTime > 0 && remainingQuantum > 0) {
                    Thread.sleep(unitOfTime);

                    if (running) {
                        burstTime--;
                        remainingQuantum--;

                        System.out.println(
                                "FCAIProcess " +
                                        name +
                                        " running -> remainingQuantum:" +
                                        remainingQuantum +
                                        " burstTime: " +
                                        burstTime +
                                        " FCAI Factor: " + calc.calcFactor(this)
                        );
                        scheduler.highlightProcessRow(number, color);
                        // System.out.println(
                        //   "FCAIProcess " +
                        //   name +
                        //   " running -> remainingQuantum:" +
                        //   remainingQuantum +
                        //   " burstTime: " +
                        //   burstTime
                        // );

                        if (running && isPreemptive()) {
                            boolean signal = scheduler.signal();
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

                boolean signal = scheduler.signal();
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