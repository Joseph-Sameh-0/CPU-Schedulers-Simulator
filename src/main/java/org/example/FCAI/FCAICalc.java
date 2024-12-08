package org.example.FCAI;

// import org.example.data.Process;

public class FCAICalc {

  final double v1;
  final double v2;

  FCAICalc(int lastArrivalTime, int maxBurstTime) {
    this.v1 = (double) lastArrivalTime / 10;
    this.v2 = (double) maxBurstTime / 10;
  }

  int calcFactor(FCAIProcess p) {
    double factor =
      (10 - p.getPriority()) +
      Math.ceil((p.getArrivalTime() / v1)) +
      Math.ceil((p.getBurstTime() / v2));

    return ((int) Math.ceil(factor));
  }

  public static void updateQuantum(FCAIProcess p) {
    if (p.getRemainingQuantum() == 0) {
      p.setQuantum(p.getQuantum() + 2);
    } else {
      p.setQuantum(p.getQuantum() + p.getRemainingQuantum());
    }
  }

  public static Boolean isPreemptive(FCAIProcess p) {
    double preemptiveFactor =
      (p.getQuantum() - p.getRemainingQuantum()) / (double) p.getQuantum();
    // System.out.println("preemptiveFactor = " + preemptiveFactor);
    if (preemptiveFactor >= 0.4) {
      // System.out.println(p.getName() + " is Preemptive");
      return true;
    }
    // System.out.println(p.getName() + " is not Preemptive");
    return false;
  }
}
