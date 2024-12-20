
package org.example.SRTF;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SRTFSimulator {

    private SRTFSchedular schedular;
    private List<SRTFProcess> processes;

    public SRTFSimulator(int processCount) {
        processes = new ArrayList<>();
        schedular = new SRTFSchedular(processCount);

        new Thread(schedular).start();
    }

    public void setProcesses(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                String processName = parts[0];
                int burstTime = Integer.parseInt(parts[1]);
                int arrivalTime = Integer.parseInt(parts[2]);
                String colorHex = parts[4]; // The hexadecimal color string from the file

                // Convert the hexadecimal string to a Color object
                Color processColor = getColorFromHex(colorHex);

                processes.add(new SRTFProcess(processes.size() + 1, processName, burstTime, arrivalTime, schedular, processColor));
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


    public void run() {
        for (SRTFProcess p : processes) {
            new Thread(p).start();
        }
    }

    public static void main() {
        SRTFSimulator Mainscheduler = new SRTFSimulator(8);

        // Read processes from file
        Mainscheduler.setProcesses("src/test_cases/SRTF.txt");

        Mainscheduler.run();

    }
}
