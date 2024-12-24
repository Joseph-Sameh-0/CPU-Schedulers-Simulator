package org.example.FCAI;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.example.ColoredRectangle;

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
    private int currTime;
    private final Map<Integer, List<ColoredRectangle>> highlightedRows = new HashMap<>();

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

                        for (int time = 0; time <= currTime; time++) { // Example: 100 ctime units
                            int x = time * cellWidth;
                            g.drawString(String.valueOf(time), x + 15, 15); // Draw ctime labels
                            g.drawLine(x, 20, x, getHeight()); // Vertical lines for grid
                        }

                        // Draw process rows and labels
                        for (FCAIProcess p : processes) {
                            // int rowY = p.getNumber() * 40; // Offset by top row height
                            // g.setColor(Color.WHITE);
                            // g.drawString(p.getName(), 10, rowY + 15);

                            // Draw rectangles for this process
                            if (highlightedRows.containsKey(p.getNumber())) {
                                for (ColoredRectangle rect : highlightedRows.get(p.getNumber())) {
                                    g.setColor(rect.color);
                                    g.fillRect(rect.x, rect.y, rect.width, rect.height);
                                    g.setColor(Color.BLACK);
                                    g.fillRect(rect.x + rect.width - 2, rect.y, 2, rect.height);
                                }
                            }
                        }

                        // Dynamically adjust preferred size
                        int maxWidth = cellWidth * currTime + 200; // Adjust based on total time
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
                for (FCAIProcess p : processes) {
                    g.drawString(p.getName(), 10, p.getNumber() * 40 + 15);
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
        for (FCAIProcess p : processes) {
            JLabel processInfo = new JLabel(
                    "Process: " + p.getName() + ", Color: " + p.getColor()
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

    public synchronized void highlightProcessRow(int processNumber, Color color) {
        final int squareWidth = 50;
        final int squareHeight = 20;
        final int y = processNumber * 40; // Calculate the y-coordinate for the row
        int x = currTime * squareWidth; // Calculate the x-coordinate based on time

        // Append a rectangle to the process row
        highlightedRows
                .computeIfAbsent(processNumber, k -> new ArrayList<>()) // Create a list if absent
                .add(new ColoredRectangle(x, y, squareWidth, squareHeight, color));

        SwingUtilities.invokeLater(() -> {
            Rectangle rect = new Rectangle(x, y, squareWidth, squareHeight);
            graphPanel.scrollRectToVisible(rect);
        });

        graphPanel.repaint(); // Trigger repaint to redraw all rectangles
    }

    public synchronized void deHighlightProcessRow(int processNumber) {
        highlightedRows.get(processNumber).remove(highlightedRows.get(processNumber).size() - 1);
    }


    public void main() {
        setupGUI();

        new Thread(() -> {
            currTime = 0;
            while (finishedProcesses < processes.size()) {
                try {
                    Thread.sleep(unitOfTime / 2);
                    System.out.println(
                            "------------------------------------------------------------------------- time " +
                                    currTime +
                                    " -> " +
                                    (currTime + 1)
                    );
                    currTime++;
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
                String colorHex = parts[5]; // The hexadecimal color string from the file

                // Convert the hexadecimal string to a Color object
                Color processColor = getColorFromHex(colorHex);

                processes.add(
                        new FCAIProcess(
                                processes.size() + 1,
                                processName,
                                burstTime,
                                arrivalTime,
                                priority,
                                quantum,
                                processColor, // Use the color parsed from the file
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

    // Method to convert a hexadecimal color string to a Color object
    private Color getColorFromHex(String hexColor) {
        try {
            return Color.decode(hexColor); // Convert the hex string to a Color object
        } catch (NumberFormatException e) {
            return Color.GRAY; // Default color if the hex code is invalid
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
                    deHighlightProcessRow(runningProcess.getNumber());
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
                    deHighlightProcessRow(runningProcess.getNumber());
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
                    deHighlightProcessRow(runningProcess.getNumber());
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
                deHighlightProcessRow(runningProcess.getNumber());
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
