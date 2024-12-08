package org.example;

import org.example.FCAI.*;


public class CPUScheduler {

  static FCAISchedular schedular;

  public static void main(String[] args) {
    new Thread(() -> {
      int g = 40;
      while (g-- > 0) {
        try {
          Thread.sleep(500);
          System.out.println(
            "------------------------------------------------------------------------- time " + (39 - g) + " -> " +(40 - g)
          );
          Thread.sleep(500);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        }
      }
    })
      .start();

    schedular = new FCAISchedular();

    //get processes
    schedular.setProcesses("src/test_cases/AG.txt");
    // List<Process> processes = getProcesses();

    // schedular.setProcesses(processes);
    //start

    schedular.run();
  }
}
