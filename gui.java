import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//Process class to store process details
class Process {
    String name;
    int arrivalTime, burstTime, waitingTime, turnaroundTime, priority;
    boolean completed;

    Process(String name, int arrivalTime, int burstTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.completed = false;
        this.priority = 1;
    }
}

public class gui extends JFrame {
    private JPanel graphPanel;
    private JPanel infoPanel;
    private JPanel statsPanel;
    private List<Process> processes;

    // Constructor to initialize the GUI
    public gui(List<Process> processes) {
        this.processes = processes;
        setTitle("CPU Scheduling Graph");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize and configure the graph panel
        graphPanel = new JPanel() {
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

    // Method to draw the scheduling graph
    private void drawGraph(Graphics g) {
        int y = 20; // Initial y-coordinate
        for (Process p : processes) {
            g.setColor(getColorForProcess(p.name)); // Set color based on process name
            g.fillRect(50, y, p.burstTime * 10, 20); // Draw rectangle for process
            g.setColor(Color.WHITE);
            g.drawString(p.name, 55, y + 15); // Draw process name
            y += 30; // Increment y-coordinate for next process
        }
    }

    // Method to get color based on process name
    private Color getColorForProcess(String name) {
        switch (name) {
            case "HP1": return Color.YELLOW;
            case "MP1": return Color.RED;
            case "LP1": return Color.MAGENTA;
            default: return Color.CYAN;
        }
    }

    // Method to setup the info panel
    private void setupInfoPanel() {
        JLabel title = new JLabel("Processes Information");
        title.setForeground(Color.RED);
        infoPanel.add(title);

        // Add each process's information
        for (Process p : processes) {
            JLabel processInfo = new JLabel("Process: " + p.name + ", Color: " + getColorForProcess(p.name));
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
        JLabel awtLabel = new JLabel("AWT: " + calculateAverageWaitingTime());
        awtLabel.setForeground(Color.WHITE);
        statsPanel.add(awtLabel);

        // Display average turnaround time
        JLabel aiatLabel = new JLabel("AIAT: " + calculateAverageTurnaroundTime());
        aiatLabel.setForeground(Color.WHITE);
        statsPanel.add(aiatLabel);
    }

    // Method to calculate average waiting time
    private double calculateAverageWaitingTime() {
        int totalWaitingTime = processes.stream().mapToInt(p -> p.waitingTime).sum();
        return (double) totalWaitingTime / processes.size();
    }

    // Method to calculate average turnaround time
    private double calculateAverageTurnaroundTime() {
        int totalTurnaroundTime = processes.stream().mapToInt(p -> p.turnaroundTime).sum();
        return (double) totalTurnaroundTime / processes.size();
    }

    // Main method to run the application
    public static void main(String[] args) {
        ArrayList<Process> processes = new ArrayList<>();
        processes.add(new Process("HP1", 0, 5));
        processes.add(new Process("MP1", 1, 3));
        processes.add(new Process("LP1", 2, 8));
        // Add more processes as needed

        new gui(processes);
    }
}
