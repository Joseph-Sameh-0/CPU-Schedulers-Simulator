package org.example.FCAI;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class FCAISchedular extends JFrame {

  FCAICalc calc;
  FCAIProcess runningProcess;
  private final CustomQueue<FCAIProcess> waitingQueue;
  protected List<FCAIProcess> processes;
  private int finishedProcesses;
  private JPanel graphPanel;
  private JPanel infoPanel;
  private JPanel statsPanel;
  private final long unitOfTime;

  public FCAISchedular(long unitOfTime) {
    this.waitingQueue = new CustomQueue<>();
    this.unitOfTime = unitOfTime;
  }

  public FCAISchedular() {
    this(1000);
  }

  private void setupGUI() {
    //setup the GUI
    //set the processes information in the GUI
    setTitle("CPU Scheduling Graph");
    setSize(800, 600);
    // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    // Initialize and configure the graph panel
    graphPanel =
      new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);
          drawGraph(g); // Custom method to draw the graph
        }
      };


    graphPanel.setBackground(Color.DARK_GRAY);
    graphPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
    add(graphPanel, BorderLayout.CENTER);

    // Initialize and configure the info panel
    infoPanel = new JPanel();
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setBackground(Color.DARK_GRAY);
    infoPanel.setPreferredSize(new Dimension(200, getHeight()));
    infoPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
    infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    add(infoPanel, BorderLayout.EAST);

    // Initialize and configure the stats panel
    statsPanel = new JPanel();
    statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
    statsPanel.setBackground(Color.DARK_GRAY);
    statsPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
    statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    add(statsPanel, BorderLayout.SOUTH);

    setupInfoPanel(); // Method to populate the info panel
    setupStatsPanel(); // Method to populate the stats panel

    setVisible(true);
  }

  public void main() {
    setupGUI();

    new Thread(() -> {
      int Time = 0;
      while (finishedProcesses < processes.size()) {
        try {
          Thread.sleep(unitOfTime / 2);
          System.out.println(
            "------------------------------------------------------------------------- time " +
            Time +
            " -> " +
            (Time + 1)
          );
          Time++;
          Thread.sleep(unitOfTime / 2);
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

  // Method to setup the info panel
  private void setupInfoPanel() {
    JLabel title = new JLabel("Processes Information");
    title.setForeground(Color.RED);
    infoPanel.add(title);

    // Add each process's information
    for (FCAIProcess p : processes) {
      JLabel processInfo = new JLabel(
        "Process: " + p.getName() + ", Color: " + p.getColor()
      );
      processInfo.setForeground(Color.WHITE);
      infoPanel.add(processInfo);
    }
  }

  // Method to setup the stats panel
  private void setupStatsPanel() {
    JLabel statsTitle = new JLabel("Statistics");
    statsTitle.setForeground(Color.RED);
    statsPanel.add(statsTitle);

    // Display average waiting time
    // JLabel awtLabel = new JLabel("AWT: " + calculateAverageWaitingTime());
    JLabel awtLabel = new JLabel("AWT: " + 55);
    awtLabel.setForeground(Color.WHITE);
    statsPanel.add(awtLabel);

    // Display average turnaround time
    // JLabel aiatLabel = new JLabel("AIAT: " + calculateAverageTurnaroundTime());
    JLabel aiatLabel = new JLabel("AIAT: " + 66);
    aiatLabel.setForeground(Color.WHITE);
    statsPanel.add(aiatLabel);
  }

  // Method to draw the scheduling graph
  private void drawGraph(Graphics g) {
    int counter = 0;
    final int squareWidth = 50;
    final int squareHieght = 20;
    final int separatorWidth = 2;

    for (FCAIProcess p : processes) {
      int counter2 = 0;
      int y = p.getNumber() * 20 + counter; // Initial y-coordinate
      for (int i = 0; i < p.getBurstTime() * 20; i += 20) {
        g.setColor(p.getColor()); // Set color based on process name
        g.fillRect(50 + (p.getArrivalTime()*50) + i + counter2 + 1, y, squareWidth, squareHieght); // Draw rectangle for process
        g.setColor(Color.black); // Set color based on process name
        g.fillRect(50 + (p.getArrivalTime()*50) + i + 20, y,separatorWidth, squareHieght); // Draw rectangle for process
        counter2 ++;
      }
      g.setColor(Color.WHITE);
      g.drawString(p.getName(), 55, y + 15); // Draw process name
      counter += 5;
      // y += 30; // Increment y-coordinate for next process
    }
  }

  // // Method to draw the scheduling graph
  // private void drawRectangle(Graphics g) {
  //   for (FCAIProcess p : processes) {
  //     int y = p.getNumber() * 20; // Initial y-coordinate
  //     g.setColor(p.getColor()); // Set color based on process name
  //     g.fillRect(50, y, p.getBurstTime() * 10, 20); // Draw rectangle for process
  //     g.setColor(Color.WHITE);
  //     g.drawString(p.getName(), 55, y + 15); // Draw process name
  //     y += 30; // Increment y-coordinate for next process
  //   }
  // }

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
            processes.size() + 1,
            processName,
            burstTime,
            arrivalTime,
            priority,
            quantum,
            Color.red, ///
            unitOfTime,
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
    if (runningProcess != null) {
      if (!waitingQueue.isEmpty()) {
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
          System.out.println(
            "FCAI process " + runningProcess.getName() + " interrupted"
          );
          waitingQueue.add(runningProcess);
          runningProcess = waitingQueue.removeSmallest();
          new Thread(runningProcess).start();
          return true;
        } else {
          return false;
        }
      } else if (
        runningProcess.getRemainingQuantum() == 0 &&
        runningProcess.getBurstTime() != 0
      ) {
        return false;
      } else if (runningProcess.getBurstTime() == 0) {
        System.out.println(
          "FCAIprocess " + runningProcess.getName() + " finished"
        );
        finishedProcesses++;
        runningProcess = null;
        return true;
      } else {
        return false;
      }
    }
    if (!waitingQueue.isEmpty()) {
      runningProcess = waitingQueue.removeFirstIn();
      new Thread(runningProcess).start();
      return true;
    }
    return false;
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

  private final int number;
  private final String name;
  private int burstTime;
  private final int arrivalTime;
  private final int priority;
  private int quantum;
  private FCAISchedular schedular;
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
    this.schedular = schedular;
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
