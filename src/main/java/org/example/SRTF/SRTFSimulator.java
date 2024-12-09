
package org.example.SRTF;
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

                processes.add(new SRTFProcess(processName, burstTime, arrivalTime, schedular));
            }
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
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
