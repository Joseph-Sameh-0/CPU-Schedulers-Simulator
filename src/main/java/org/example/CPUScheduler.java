package org.example;

import org.example.FCAI.*;

public class CPUScheduler {

  static FCAISchedular schedular;

  public static void main(String[] args) {
    schedular = new FCAISchedular();
    schedular.setProcesses("src/test_cases/AG.txt");
    schedular.run();
  }
}
