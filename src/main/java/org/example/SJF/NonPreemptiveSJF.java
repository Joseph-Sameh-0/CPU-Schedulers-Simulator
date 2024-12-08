package org.example.SJF;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;

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

public class NonPreemptiveSJF {

  public static void simulateSJF(ArrayList<Process> processes) {
    int currentTime = 0, completed = 0;
    int totalWaitingTime = 0, totalTurnaroundTime = 0;

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

  public static void main() {
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
        processes.add(new Process(name, arrival, burst));
      }
    } catch (Exception e) {
      System.err.println("Error reading the file: " + e.getMessage());
      return;
    }

    simulateSJF(processes);
  }
}
