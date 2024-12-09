package org.example.SJF;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.example.ColoredRectangle;

class Process {

  String name;
  int arrivalTime, burstTime, waitingTime, turnaroundTime, priority;
  boolean completed;
  final int number;
  final Color color;

  Process(
    int number,
    String name,
    int arrivalTime,
    int burstTime,
    Color color
  ) {
    this.number = number;
    this.name = name;
    this.arrivalTime = arrivalTime;
    this.burstTime = burstTime;
    this.completed = false;
    this.priority = 1;
    this.color = color;
  }
}

public class NonPreemptiveSJF extends JFrame {

  private JPanel graphPanel;
  private JPanel infoPanel;
  private JPanel statsPanel;
  private final Map<Integer, List<ColoredRectangle>> highlightedRows = new HashMap<>();

  public void simulateSJF(ArrayList<Process> processes) {
    int currentTime = 0, completed = 0;
    int totalWaitingTime = 0, totalTurnaroundTime = 0;
    setupGUI(processes);
    //put processes into ready queue
    while (completed < processes.size()) {
      ArrayList<Process> availableProcesses = new ArrayList<>();
      for (Process p : processes) {
        if (p.arrivalTime <= currentTime && !p.completed) {
          availableProcesses.add(p);
        }
      }
      // --------------------------------------------------------------------------------------------------------------------
      // Check if no process have arrived

      if (availableProcesses.isEmpty()) {
        currentTime++;
        continue;
      }
      // --------------------------------------------------------------------------------------------------------------------
      // Starvation Solution

      for (Process p : processes) {
        if (!p.completed && currentTime - p.arrivalTime > 10) {
          p.priority = 0;
        }
      }
      availableProcesses.sort(
        Comparator
          .comparingInt((Process p) -> p.priority)
          .thenComparingInt(p -> p.burstTime)
      );
      // --------------------------------------------------------------------------------------------------------------------
      // start process in processor

      Process process = availableProcesses.get(0);
      currentTime += process.burstTime;
      process.completed = true;
      completed++;

      // --------------------------------------------------------------------------------------------------------------------
      // calculate time

      process.turnaroundTime = currentTime - process.arrivalTime;
      process.waitingTime = process.turnaroundTime - process.burstTime;
      totalWaitingTime += process.waitingTime;
      totalTurnaroundTime += process.turnaroundTime;
      System.out.println(
        "Process " +
        process.name +
        " executed. " +
        "Waiting Time: " +
        process.waitingTime +
        ", Turnaround Time: " +
        process.turnaroundTime
      );
      highlightProcessRow(process);
    }

    System.out.println(
      "\nAverage Turnaround Time: " +
      (double) totalTurnaroundTime /
      processes.size()
    );
    System.out.println(
      "Average Waiting Time: " + (double) totalWaitingTime / processes.size()
    );
  }

  public void main() {
    ArrayList<Process> processes = new ArrayList<>();

    try (
      BufferedReader br = new BufferedReader(
        new FileReader("src/test_cases/Process.txt")
      )
    ) {
      int n = Integer.parseInt(br.readLine().trim());

      for (int i = 0; i < n; i++) {
        String[] processDetails = br.readLine().split(" ");
        String name = processDetails[0];
        int arrival = Integer.parseInt(processDetails[1]);
        int burst = Integer.parseInt(processDetails[2]);
        String colorHex = processDetails[4]; // The hexadecimal color string from the file
        Color processColor = getColorFromHex(colorHex);
        processes.add(new Process(i + 1, name, arrival, burst, processColor));
      }
    } catch (Exception e) {
      System.err.println("Error reading the file: " + e.getMessage());
      return;
    }

    simulateSJF(processes);
  }

  void setupGUI(ArrayList<Process> processes) {
    //setup the GUI
    //set the processes information in the GUI
    setTitle("CPU Scheduling Graph");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setLayout(new BorderLayout());

    // Center the window on the screen
    setLocationRelativeTo(null);

    // Ensure the window appears above others when launched
    setAlwaysOnTop(true); // Keep it on top during setup
    setVisible(true); // Make it visible
    toFront(); // Bring to the front
    // setAlwaysOnTop(false); // Allow normal window behavior afterward

    graphPanel =
      new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
          super.paintComponent(g);

          int cellWidth = 50; // Width of each time unit

          // Draw the top time row
          g.setColor(Color.WHITE);

          for (int time = 0; time <= 100; time++) { // Example: 100 ctime units
            int x = time * cellWidth;
            g.drawString(String.valueOf(time), x + 15, 15); // Draw ctime labels
            g.drawLine(x, 20, x, getHeight()); // Vertical lines for grid
          }

          // Draw process rows and labels
          for (Process p : processes) {
            // int rowY = p.getNumber() * 40; // Offset by top row height
            // g.setColor(Color.WHITE);
            // g.drawString(p.getName(), 10, rowY + 15);

            // Draw rectangles for this process
            if (highlightedRows.containsKey(p.number)) {
              for (ColoredRectangle rect : highlightedRows.get(p.number)) {
                g.setColor(rect.color);
                g.fillRect(rect.x, rect.y, rect.width, rect.height);
                g.setColor(Color.BLACK);
                g.fillRect(rect.x + rect.width - 2, rect.y, 2, rect.height);
              }
            }
          }

          // Dynamically adjust preferred size
          int maxWidth = cellWidth * 100 + 200; // Adjust based on total time
          int maxHeight = processes.size() * 40 + 60; // Account for rows + top row
          setPreferredSize(new Dimension(maxWidth, maxHeight));
          revalidate();
        }
      };

    graphPanel.setBackground(Color.DARK_GRAY);
    graphPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
    add(graphPanel, BorderLayout.CENTER);

    // Create the panel for process names
    JPanel processNamesPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        for (Process p : processes) {
          g.drawString(p.name, 10, p.number * 40 + 15);
        }
      }
    };
    processNamesPanel.setBackground(Color.DARK_GRAY);
    processNamesPanel.setPreferredSize(new Dimension(50, 800)); // Fixed width for names

    // Wrap the graphPanel in a JScrollPane
    JScrollPane graphScrollPane = new JScrollPane(graphPanel);
    graphScrollPane.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
    );
    graphScrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
    );

    // Synchronize vertical scrolling between graphScrollPane and processNamesPanel
    graphScrollPane
      .getVerticalScrollBar()
      .addAdjustmentListener(e -> {
        processNamesPanel.repaint(); // Repaint names to stay aligned with the graph
      });

    // Combine the two panels (process names and scrollable graph)
    JPanel combinedPanel = new JPanel(new BorderLayout());
    combinedPanel.add(processNamesPanel, BorderLayout.WEST); // Pinned names on the left
    combinedPanel.add(graphScrollPane, BorderLayout.CENTER); // Scrollable graph on the right

    // Add combinedPanel to the frame
    add(combinedPanel, BorderLayout.CENTER);

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

    // Method to setup the info panel
    JLabel title = new JLabel("Processes Information");
    title.setForeground(Color.RED);
    infoPanel.add(title);

    // Add each process's information
    for (Process p : processes) {
      JLabel processInfo = new JLabel(
        "Process: " + p.name + ", Color: " + p.color
      );
      processInfo.setForeground(Color.WHITE);
      infoPanel.add(processInfo);
    }

    JLabel statsTitle = new JLabel("Statistics");
    statsTitle.setForeground(Color.RED);
    statsPanel.add(statsTitle);

    // Display average waiting time
    // JLabel awtLabel = new JLabel("AWT: " + calculateAverageWaitingTime());
    JLabel awtLabel = new JLabel("AWT: " + 55);
    awtLabel.setForeground(Color.WHITE);
    statsPanel.add(awtLabel);

    // Display average turnaround time
    // JLabel aIATLabel = new JLabel("AIAT: " + calculateAverageTurnaroundTime());
    JLabel aIATLabel = new JLabel("AIAT: " + 66);
    aIATLabel.setForeground(Color.WHITE);
    statsPanel.add(aIATLabel);

    setVisible(true);
  }

  public synchronized void highlightProcessRow(Process p) {
    final int squareWidth = 50;
    final int squareHeight = 20;
    int startPosition = (p.arrivalTime + p.waitingTime) * squareWidth; // Calculate the x-coordinate based on time
    final int y = p.number * 40; // Calculate the y-coordinate for the row

    for (int i = 0; i < p.waitingTime; i++) {
      // Append a rectangle to the process row
      highlightedRows
        .computeIfAbsent(p.number, k -> new ArrayList<>()) // Create a list if absent
        .add(
          new ColoredRectangle(
            (p.arrivalTime + i) * squareWidth,
            y,
            squareWidth,
            squareHeight,
            modifyColor(p.color)
          )
        );
    }

    for (int i = 0; i < p.burstTime; i++) {
      // Append a rectangle to the process row
      highlightedRows
        .computeIfAbsent(p.number, k -> new ArrayList<>()) // Create a list if absent
        .add(
          new ColoredRectangle(
            startPosition + i * squareWidth,
            y,
            squareWidth,
            squareHeight,
            p.color
          )
        );
    }

    // SwingUtilities.invokeLater(() -> {
    //   Rectangle rect = new Rectangle(x, y, squareWidth, squareHeight);
    //   graphPanel.scrollRectToVisible(rect);
    // });

    graphPanel.repaint(); // Trigger repaint to redraw all rectangles
  }

  private Color modifyColor(Color originalColor) {
    // Extract the RGB components from the original color
    int r = originalColor.getRed();
    int g = originalColor.getGreen();
    int b = originalColor.getBlue();

    // Modify the color (you can change this formula to anything you'd like)
    int newR = (r + 50) % 256; // Adjust the red component and ensure it stays within the valid range
    int newG = (g + 100) % 256; // Adjust the green component
    int newB = (b + 150) % 256; // Adjust the blue component

    // Return a new color with the modified components
    return new Color(newR, newG, newB);
  }

  private static Color getColorFromHex(String hexColor) {
    try {
      return Color.decode(hexColor); // Convert the hex string to a Color object
    } catch (NumberFormatException e) {
      return Color.GRAY; // Default color if the hex code is invalid
    }
  }
  // Method to convert a hexadecimal color string to a Color object
}
