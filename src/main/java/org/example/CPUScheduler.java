package org.example;

import java.util.Scanner;
import org.example.FCAI.*;

public class CPUScheduler {

  public static void main(String[] args) {
    // get the user's choice
    Scanner scanner = new Scanner(System.in);
    boolean exit = false;
    while (!exit) {
      //let the user select the scheduler
      System.out.println("Select a scheduler: ");
      System.out.println("1. Shortest- Job First (SJF) Scheduling");
      System.out.println("2. SRTF Scheduling");
      System.out.println("3. Priority Scheduling");
      System.out.println("4. FCAI Scheduling");
      System.out.println("5. Exit");
      int choice = scanner.nextInt();
      // create a new scheduler based on the user's choice
      switch (choice) {
        case 1:
          // schedular = new SJFScheduler(); // create a new SJF scheduler
          break;
        case 2:
          // schedular = new SRTFScheduler(); // create a new SRTF scheduler
          break;
        case 3:
          // schedular = new PriorityScheduler(); // create a new priority scheduler
          break;
        case 4:
          final FCAISchedular fCAISchedular;
          fCAISchedular = new FCAISchedular(); // create a new FCAI scheduler
          fCAISchedular.setProcesses("src/test_cases/AG.txt");
          // new Thread(fCAISchedular).start();
          fCAISchedular.main(scanner); // run the FCAI scheduler

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
