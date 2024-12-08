package org.example.tsts;

import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class FCUISchedulerGUI {

  public FCUISchedulerGUI() {
    // Main Frame Setup
    JFrame frame = new JFrame("CPU Scheduler");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1200, 800);
    frame.setLayout(new BorderLayout());

    // // Left Panel: CPU Scheduling Graph
    // JPanel graphPanel = new JPanel();
    // graphPanel.setLayout(new BorderLayout());
    // graphPanel.setBorder(
    //   BorderFactory.createTitledBorder("CPU Scheduling Graph")
    // );

    JScrollPane graphScrollPane = new JScrollPane();
    graphScrollPane.setHorizontalScrollBarPolicy(
      JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
    );
    graphScrollPane.setVerticalScrollBarPolicy(
      JScrollPane.VERTICAL_SCROLLBAR_NEVER
    );
    graphScrollPane.setPreferredSize(new Dimension(800, 600));

    // Left Panel: CPU Scheduling Graph
    JPanel graphPanel = new JPanel() {
      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw horizontal dotted lines for time slices
        for (int y = 50; y <= 400; y += 50) {
          float[] dashPattern = { 5, 5 };
          g2d.setStroke(
            new BasicStroke(
              1,
              BasicStroke.CAP_BUTT,
              BasicStroke.JOIN_BEVEL,
              0,
              dashPattern,
              0
            )
          );
          g2d.draw(new Line2D.Float(0, y, 2000, y));
        }

        // Draw vertical dotted lines to partition processes
        for (int x = 0; x <= 2000; x += 200) {
          float[] dashPattern = { 5, 5 };
          g2d.setStroke(
            new BasicStroke(
              1,
              BasicStroke.CAP_BUTT,
              BasicStroke.JOIN_BEVEL,
              0,
              dashPattern,
              0
            )
          );
          g2d.draw(new Line2D.Float(x, 0, x, 500));
        }

        // Draw process names on the left
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 6; i++) {
          g2d.drawString("P" + i, 10, 50 + (i * 50));
        }
      }
    };

    graphPanel.setLayout(null); // Custom drawing
    graphPanel.setPreferredSize(new Dimension(2000, 500));
    graphPanel.setBackground(Color.WHITE);

    // Animation logic
    Timer timer = new Timer(100, e -> animateGraph(graphPanel));
    timer.start();

    // Sample graph rendering (replace with actual graph drawing logic)
    for (int i = 0; i < 6; i++) {
      JLabel processBar = new JLabel();
      processBar.setOpaque(true);
      processBar.setBackground(new Color((int) (Math.random() * 0x1000000)));
      processBar.setBounds(i * 200, 50 + (i * 50), 150, 30); // Position dynamically
      graphPanel.add(processBar);
    }

    graphScrollPane.setViewportView(graphPanel);

    // Right Panel: Process Information Table
    JPanel tablePanel = new JPanel();
    tablePanel.setLayout(new BorderLayout());
    tablePanel.setBorder(
      BorderFactory.createTitledBorder("Processes Information")
    );

    String[] columns = { "PROCESS", "COLOR", "NAME", "PRIORITY" };
    DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
    JTable processTable = new JTable(tableModel);

    // Sample Data (replace with actual process data)
    for (int i = 0; i < 6; i++) {
      String processName = "P" + i;
      String priority = String.valueOf(10 * i);
      Color color = new Color((int) (Math.random() * 0x1000000));
      tableModel.addRow(new Object[] { i, color, processName, priority });
    }

    JScrollPane tableScrollPane = new JScrollPane(processTable);
    tablePanel.add(tableScrollPane, BorderLayout.CENTER);

    // Bottom Panel: Statistics
    JPanel statsPanel = new JPanel();
    statsPanel.setLayout(new GridLayout(3, 1));
    statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
    statsPanel.setBackground(Color.DARK_GRAY);

    JLabel scheduleLabel = new JLabel(
      "Schedule Name: ABC Schedule",
      JLabel.LEFT
    );
    scheduleLabel.setForeground(Color.RED);
    JLabel awtLabel = new JLabel("AWT: 3125", JLabel.LEFT);
    awtLabel.setForeground(Color.RED);
    JLabel atatLabel = new JLabel("ATAT: 12331", JLabel.LEFT);
    atatLabel.setForeground(Color.RED);

    statsPanel.add(scheduleLabel);
    statsPanel.add(awtLabel);
    statsPanel.add(atatLabel);

    // Adding panels to frame
    frame.add(graphPanel, BorderLayout.CENTER);
    frame.add(tablePanel, BorderLayout.EAST);
    frame.add(statsPanel, BorderLayout.SOUTH);

    frame.setVisible(true);
  }

  private int animationX = 0;
  private boolean movingRight = true;

  private void animateGraph(JPanel graphContent) {
    // Increment or decrement animation position
    if (movingRight) {
      animationX += 10;
      if (animationX >= 1000) {
        movingRight = false; // Reverse direction
      }
    } else {
      animationX -= 10;
      if (animationX <= 0) {
        movingRight = true; // Reverse direction
      }
    }

    // Update the position of a sample process part
    for (Component component : graphContent.getComponents()) {
      if (component instanceof JLabel) {
        JLabel processBar = (JLabel) component;
        processBar.setLocation(animationX, processBar.getY());
      }
    }

    graphContent.repaint();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(FCUISchedulerGUI::new);
  }
}
