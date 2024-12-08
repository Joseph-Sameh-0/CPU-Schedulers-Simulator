import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

class Process {
    String name;
    int arrivalTime, burstTime, waitingTime, turnaroundTime, priority, completed;

    Process(String name, int arrivalTime, int burstTime, int priority) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.completed = 0; 
    }
}

public class NonPreemptivePriority {
    public static void simulatePriority(ArrayList<Process> processes, int contextSwitchTime) {
        int currentTime = 0;
        int completedProcesses = 0;
        int totalTurnaroundTime = 0;
        int totalWaitingTime = 0;

        while (completedProcesses < processes.size()) {
            ArrayList<Process> availableProcesses = new ArrayList<>();

            for (Process p : processes) {
                if (p.arrivalTime <= currentTime && p.completed == 0) {
                    availableProcesses.add(p);
                }
            }

            if (availableProcesses.isEmpty()) {
                currentTime++;
                continue;
            }

            availableProcesses.sort((p1, p2) -> {
                if (p1.priority == p2.priority) {
                    return Integer.compare(p1.arrivalTime, p2.arrivalTime);
                } else {
                    return Integer.compare(p1.priority, p2.priority);
                }
            });

            Process process = availableProcesses.get(0);

            currentTime += process.burstTime + contextSwitchTime; 
            process.completed = 1;
            completedProcesses++;

            process.turnaroundTime = currentTime - contextSwitchTime - process.arrivalTime; 
            process.waitingTime = process.turnaroundTime - process.burstTime ;

            totalWaitingTime += process.waitingTime;
            totalTurnaroundTime += process.turnaroundTime;

            System.out.println("Process " + process.name + " executed. " +
                    "Priority: " + process.priority +
                    ", Waiting Time: " + process.waitingTime +
                    ", Turnaround Time: " + process.turnaroundTime);
        }

        System.out.println("\nAverage Turnaround Time: " + (double) totalTurnaroundTime / processes.size());
        System.out.println("Average Waiting Time: " + (double) totalWaitingTime / processes.size());
    }

    public static void main(String[] args) {
        ArrayList<Process> processes = new ArrayList<>();
        int contextSwitchTime = 1; // Fixed context switching time in ms

        // Read process details from file
        try (BufferedReader br = new BufferedReader(new FileReader("Process.txt"))) {
            int n = Integer.parseInt(br.readLine().trim()); // Read number of processes

            for (int i = 0; i < n; i++) {
                String[] processDetails = br.readLine().split(" ");
                String name = processDetails[0];
                int arrival = Integer.parseInt(processDetails[1]);
                int burst = Integer.parseInt(processDetails[2]);
                int priority = Integer.parseInt(processDetails[3]);
                processes.add(new Process(name, arrival, burst, priority));
            }
        } catch (Exception e) {
            System.err.println("Error reading the file: " + e.getMessage());
            return;
        }

        simulatePriority(processes, contextSwitchTime);
    }
}