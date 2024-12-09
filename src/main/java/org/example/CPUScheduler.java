package org.example;

import java.util.Scanner;
import org.example.FCAI.*;
import org.example.Priority.NonPreemptivePriority;
import org.example.SJF.NonPreemptiveSJF;
import org.example.SRTF.SRTFSimulator;

public class CPUScheduler {

  public static void main(String[] args) {
    // get the user's choice
    Scanner scanner = new Scanner(System.in);
    boolean exit = false;
    while (!exit) {
      //let the user select the scheduler
      System.out.println("Select a scheduler: ");
      System.out.println("1. Priority Scheduling");
      System.out.println("2. Shortest- Job First (SJF) Scheduling");
      System.out.println("3. SRTF Scheduling");
      System.out.println("4. FCAI Scheduling");
      System.out.println("5. Exit");
      int choice = scanner.nextInt();
      // create a new scheduler based on the user's choice
      switch (choice) {
        case 1:
          NonPreemptivePriority p = new NonPreemptivePriority();
          p.main();
          break;
        case 2:
        NonPreemptiveSJF sjf = new NonPreemptiveSJF();
        sjf.main();          break;
        case 3:
          SRTFSimulator.main();
          break;
        case 4:
          final FCAISchedular fCAISchedular;
          fCAISchedular = new FCAISchedular(500); // create a new FCAI scheduler
          fCAISchedular.setProcesses("src/test_cases/AG.txt");
          fCAISchedular.main();
          break;
        case 5:
          exit = true;
          break;
        default: // if the user enters an invalid choice
          System.out.println("Invalid choice."); // exit the program
      }
    }
    scanner.close();
  }
}
