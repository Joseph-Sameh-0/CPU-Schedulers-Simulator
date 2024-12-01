package org.example.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Schedular implements Runnable {

  protected List<Process> processes;

  public List<Process> getProcesses() {
    return processes;
  }

  public abstract void process(Process process);

  public void setProcesses(String filename) {
    processes = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split(" ");
        String processName = parts[0];
        int burstTime = Integer.parseInt(parts[1]);
        int arrivalTime = Integer.parseInt(parts[2]);
        int priority = Integer.parseInt(parts[3]);
        int quantum = Integer.parseInt(parts[4]);

        processes.add(
          new Process(
            processName,
            burstTime,
            arrivalTime,
            priority,
            quantum,
            this
          )
        );
      }
    } catch (IOException e) {
      System.out.println("Error reading the file: " + e.getMessage());
    }
  }

  public void signal(){
    System.out.println("signal from scheduler");
  }
}
