package org.example;

import java.util.Scanner;
import org.example.FCAI.*;
import org.example.Priority.NonPreemptivePriority;
import org.example.SJF.NonPreemptiveSJF;

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
          NonPreemptivePriority.main();
          break;
        case 2:
          NonPreemptiveSJF.main();
          break;
        case 3:
          // schedular = new SRTFScheduler(); // create a new SRTF scheduler
          break;
        case 4:
          final FCAISchedular fCAISchedular;
          fCAISchedular = new FCAISchedular(500); // create a new FCAI scheduler
          fCAISchedular.setProcesses("src/test_cases/AG.txt");
          // take the choice of selecting the console or the gui
          boolean ExitFCAI = false;
          while (!ExitFCAI) {
            System.out.println("1. console app");
            System.out.println("2. gui app");
            System.out.println("3. Exit");
            choice = scanner.nextInt();
            // create a new scheduler based on the user's choice
            switch (choice) {
              case 1:
                fCAISchedular.main("console"); // run the FCAI scheduler
                break;
              case 2:
                new Thread(()->{fCAISchedular.main("gui");}).start();  // run the FCAI scheduler
                break;
              case 3:
              ExitFCAI = true;
                break;
              default: // if the user enters an invalid choice
                System.out.println("Invalid choice."); // exit the program
            }
          }
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
