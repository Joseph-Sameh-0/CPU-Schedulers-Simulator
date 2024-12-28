package org.example.SRTF;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.example.ColoredRectangle;

import java.util.PriorityQueue;

public class SRTFScheduler extends JFrame implements Runnable {
    private final int processCount;
    private int exitedProcessCount = 0;
    SRTFProcess runningProcess;
    private JPanel graphPanel;
    private JPanel infoPanel;
    private JPanel statsPanel;
    private JLabel processNamesLabel;
    private JPanel processNamesPanel;
    private int currTime;
    private List<String> processNames = new ArrayList<>();
    private final Map<Integer, List<ColoredRectangle>> highlightedRows = new HashMap<>();
    private Boolean guiSetuped = false;
    private boolean switching;

    // Priority queue based on the remaining time of the processes
    private PriorityQueue<SRTFProcess> waitingQueue = new PriorityQueue<>(
            (p1, p2) -> Integer.compare(p1.getRemainingTime(), p2.getRemainingTime())
    );


    public SRTFScheduler(int processCount) {
        waitingQueue = new PriorityQueue<>();
        this.processCount = processCount;
    }

    public void addToQueue(SRTFProcess process) {
        waitingQueue.add(process);
    }

    public void process(SRTFProcess process) {
        if (!guiSetuped) {
            setupGUI();
        }
        setupProcess(process);


        // System.out.println(
        //   "SRTFScheduler process the " + process.getName() + " process"
        // );

        if (runningProcess == null && waitingQueue.isEmpty()) {
            // System.out.println("No process is running");

            runningProcess = process;
            new Thread(runningProcess::execute).start();

            return;
        }
        System.out.println("Adding process to waiting queue " + process.getName());
        waitingQueue.add(process);
        // System.out.println("waiting queue size " + waitingQueue.size());
        process.startwaiting();
    }

    void processFinished(SRTFProcess process) {
        // System.out.println(
        //   "SRTFScheduler finished the " + process.getName() + " process"
        // );
        // highlightProcessRow(runningProcess.getNumber(), Color.GRAY);
        runningProcess = null;
        exitedProcessCount++;


    }

    @Override
    public void run() {
        // Start a timer and increment it every second to simulate the scheduleing
        new Thread(() -> {
            while (exitedProcessCount < processCount) {
                try {
                    Thread.sleep(500);
                    System.out.print("");
                    currTime++;
                    System.out.println("----------------------------- currTime: " + currTime);
                    Thread.sleep(500);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

        while (exitedProcessCount < processCount) {
            try {

                // System.out.println("waiting queue size " + waitingQueue.size());
                if (!waitingQueue.isEmpty() && !switching) {
                    if (runningProcess == null || runningProcess.getRemainingTime() == 0) {


                        while (runningProcess != null) ;

                        runningProcess = waitingQueue.poll();

                        contextSwitch();

                    } else {
                        // get the process with the shortest remaining time
                        SRTFProcess shortestProcess = waitingQueue.peek();
                        // print the remaining time of shortest process and running process
//                             System.out.println("shortest process remaining time " + shortestProcess.getRemainingTime());

                        // check if the shortest process is shorter than the currently running process
                        if (shortestProcess.getEffectiveRemainingTime() < runningProcess.getEffectiveRemainingTime()) {
                            // if the shortest process is shorter than the currently running process
                            // then interrupt the current process and execute the shortest process
                            System.out.println("shortest process is " + shortestProcess.getName() + " with remaining time " + shortestProcess.getRemainingTime());
                            runningProcess.running = false;
                            highlightProcessRow(runningProcess.getNumber(), Color.GRAY);

                            if (runningProcess.getRemainingTime() == 0) {
                                runningProcess = waitingQueue.poll();
                            } else {
                                runningProcess.setRemainingTime(runningProcess.getRemainingTime() - 1);
                                new Thread(runningProcess::startwaiting).start();
                                waitingQueue.add(runningProcess);
                                runningProcess = waitingQueue.poll();
                            }
                            // add the running process to the waiting queue
                            contextSwitch();
                        }
                    }
                }

                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

        }

    }

    private void contextSwitch() {
        switching = true;
        new Thread(() -> {
            System.out.println("switching....context");
            highlightProcessRow(runningProcess.getNumber(), Color.GRAY);
            System.out.println("finished....switching....context");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            new Thread(runningProcess::execute).start();

            switching = false;
        }).start();
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


    private void setupGUI() {
        guiSetuped = true;

        //set up the GUI
        //set the processes information in the GUI
        setTitle("CPU Scheduling Graph");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Center the window on the screen
        setLocationRelativeTo(null);

        // Ensure the window appears above others when launched
        setAlwaysOnTop(true); // Keep it on top during setup
        setVisible(true); // Make it visible
        toFront(); // Bring to the front
        setAlwaysOnTop(false); // Allow normal window behavior afterward

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

                        // Draw rectangles for each process
                        for (Map.Entry<Integer, List<ColoredRectangle>> entry : highlightedRows.entrySet()) {
                            for (ColoredRectangle rect : entry.getValue()) {
                                g.setColor(rect.color);
                                g.fillRect(rect.x, rect.y, rect.width, rect.height);
                                g.setColor(Color.BLACK);
                                g.fillRect(rect.x + rect.width - 2, rect.y, 2, rect.height);
                            }
                        }

                        // Dynamically adjust preferred size
                        int maxWidth = cellWidth * currTime + 200; // Adjust based on total time
                        int maxHeight = processCount * 40 + 60; // Account for rows + top row
                        setPreferredSize(new Dimension(maxWidth, maxHeight));
                        revalidate();
                    }
                };

        graphPanel.setBackground(Color.DARK_GRAY);
        graphPanel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
        add(graphPanel, BorderLayout.CENTER);


        processNamesPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.WHITE);
                for (int i = 0; i < processNames.size(); i++) {
                    g.drawString(processNames.get(i), 10, i * 40 + 15 + 40);
                }
            }
        };
        processNamesLabel = new JLabel();
        processNamesLabel.setForeground(Color.WHITE);
        processNamesPanel.add(processNamesLabel);

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
        infoPanel.setPreferredSize(new Dimension(500, getHeight()));
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

    public void setupProcess(SRTFProcess process) {
        Graphics graphPanelGraphics = graphPanel.getGraphics();
        if (graphPanelGraphics == null) return;
        int rowY = process.getNumber() * 40; // Offset by top row height
        graphPanelGraphics.setColor(Color.WHITE);
        graphPanelGraphics.drawString(process.getName(), 10, rowY + 15);
        graphPanel.repaint();
        processNames.add(process.getName());
        processNamesPanel.repaint();

//        Color color = process.getColor();
////        String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
//        String processColor = "RGB: (" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";

        // Create a table model to store the process information
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Process Name");
        tableModel.addColumn("Arrival Time");
        tableModel.addColumn("Burst Time");
        tableModel.addColumn("Color");

        // Add the process information to the table model
        tableModel.addRow(new Object[]{
                process.getName(),
                process.getArrivalTime(),
                process.getBurstTime(),
                "RGB: (" + process.getColor().getRed() + ", " + process.getColor().getGreen() + ", " + process.getColor().getBlue() + ")"
        });

        // Create a JTable component to display the table
        JTable processTable = new JTable(tableModel);

        // Set the background color of the table's rows
        processTable.setBackground(Color.LIGHT_GRAY);

        // Set the background color of the table's columns
        processTable.getTableHeader().setBackground(Color.DARK_GRAY);
        processTable.getTableHeader().setForeground(Color.WHITE);

        // Center the text in the fields
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        processTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        processTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        processTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        processTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);


        // Add the table to the info panel
        infoPanel.add(new JScrollPane(processTable));
        processTable.getParent().setBackground(Color.DARK_GRAY);
        infoPanel.revalidate();
        infoPanel.repaint();
    }
}
