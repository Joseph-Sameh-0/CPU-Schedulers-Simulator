package org.example.SRTF;


import java.util.PriorityQueue;

public class SRTFSchedular implements Runnable {
  private final int processCount;
  private int exitedProcessCount = 0;
  SRTFProcess runningProcess;
  // Priority queue based on the remaining time of the processes
  private PriorityQueue<SRTFProcess> waitingQueue = new PriorityQueue<>(
            (p1, p2) -> Integer.compare(p1.getRemainingTime(), p2.getRemainingTime())
        );



  public SRTFSchedular(int processCount) {
    waitingQueue = new PriorityQueue<>();
    this.processCount = processCount;
  }

  public void addToQueue(SRTFProcess process) {
    waitingQueue.add(process);
  }

  public void process(SRTFProcess process) {
    // System.out.println(
    //   "SRTFScheduler process the " + process.getName() + " process"
    // );

    if (runningProcess == null) {
      // System.out.println("No process is running");

      runningProcess = process;
      runningProcess.execute();
      
      return;
    }

    waitingQueue.add(process);
  }
  void processFinished(SRTFProcess process) {
    // System.out.println(
    //   "SRTFScheduler finished the " + process.getName() + " process"
    // );
    runningProcess = null;
    exitedProcessCount++;
    System.out.println("exited process count " + exitedProcessCount); 
  }
  @Override
  public void run() {
    // Start a timer and increment it every second to simulate the scheduleing
    new Thread(() -> {while (exitedProcessCount < processCount) {
      try {
        Thread.sleep(1000);
        System.out.println("-----------------------------");
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }}).start();
    while (exitedProcessCount < processCount) {
      try {
        Thread.sleep(1000);
        
        if (!waitingQueue.isEmpty()) {
          // get the process with the shortest remaining time
          SRTFProcess shortestProcess = waitingQueue.peek();
          
          

          // check if the shortest process is shorter than the currently running process
          if (
            runningProcess == null ||
            shortestProcess.getRemainingTime() < runningProcess.getRemainingTime()
          ) {
            // if the shortest process is shorter than the currently running process
            // then interrupt the current process and execute the shortest process
            System.out.println( "shortest process is " + shortestProcess.getName() + " with remaining time " + shortestProcess.getRemainingTime()); 
            waitingQueue.remove(shortestProcess);
            if (runningProcess != null) {
              runningProcess.running = false;
            }
            runningProcess = shortestProcess;
            System.out.println("switching....context");
            Thread.sleep(1000L);
            System.out.println("finished....switching....context");
            runningProcess.execute();
            
          }
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
      
    }
    
  }
}
