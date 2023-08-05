// Importing libraries and classes

import net.datastructures.Entry;
import net.datastructures.HeapAdaptablePriorityQueue;
import net.datastructures.HeapPriorityQueue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

// Main class for Process Scheduling
public class ProcessScheduling {

    // process class
    protected static class Process {
        // Process attributes
        private final int processId;
        private int priority;
        private final int duration;
        private final int arrivalTime;
        private int runTimeLeft;
        private int startTime = -1;

        private int waitTime;
        private int lastRunTime = -1;

        // constructors
        public Process() {
            processId = 0;
            priority = 0;
            duration = 0;
            arrivalTime = 0;
        }

        public Process(int processId, int priority, int duration, int arrivalTime) {
            this.processId = processId;
            this.priority = priority;
            this.duration = duration;
            this.arrivalTime = arrivalTime;
            this.runTimeLeft = duration;
        }

        // toString method to print process details
        public String toString() {
            String s = "";
            s += "Id = " + processId + ", ";
            s += "priority = " + priority + ", ";
            s += "duration = " + duration + ", ";
            s += "arrival time = " + arrivalTime;
            return s;
        }

        // Getter methods for process attributes
        public int getProcessId() {
            return processId;
        }

        public int getPriority() {
            return priority;
        }

        public int getDuration() {
            return duration;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }


        public int getRunTimeLeft() {
            return runTimeLeft;
        }

        public void setRunTimeLeft(int runTimeLeft) {
            this.runTimeLeft = runTimeLeft;
        }


        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int currentTime) {
            this.startTime = currentTime;
        }

        public void setLastRunTime(int lastRunTime) {
            this.lastRunTime = lastRunTime;
        }

        public int getWaitTime() {
            return waitTime;
        }

        public void setWaitTime(int waitTime) {
            this.waitTime = waitTime;
        }
    }

    // Max wait time for a process
    private static int maxWaitTime = 30;

    public static void main(String[] args) throws FileNotFoundException {

        // Open the input file
        Scanner s = new Scanner(new File("src/process_scheduling_in.txt"));
        // Create an output file to write the results to
        PrintWriter writer = new PrintWriter("process_scheduling_out.txt");

        // Create a priority queue to hold all processes
        HeapPriorityQueue<Integer, Process> D = new HeapPriorityQueue<>();
        // Create a list to hold all processes
        List<Process> allProcesses = new ArrayList<>();

        // Read in each line of the input file and create a Process object for each line
        while (s.hasNext()) {
            String line = s.nextLine();
            String[] lineContents = line.split(" ");
            // Add the Process to both the priority queue and the list
            Process p1 = new Process(Integer.parseInt(lineContents[0]), Integer.parseInt(lineContents[1]), Integer.parseInt(lineContents[2]), Integer.parseInt(lineContents[3]));
            D.insert(p1.getArrivalTime(), p1);
            allProcesses.add(p1);
            // Write the Process information to the output file
            writer.println(p1);
        }
        // Close the input file
        s.close();
        writer.println();
        // Print the maximum wait time so far to the output file
        writer.println("Maximum Wait time = " + maxWaitTime);
        writer.println();

        // Initialize the current time to 0
        int currentTime = 0;

        // Initialize a heap-based priority queue for processes that have arrived but not yet started
        HeapAdaptablePriorityQueue<Integer, Process> Q = new HeapAdaptablePriorityQueue<>();

        // Initialize the running process to an empty process object
        Process runningP = new Process();

        // Continue looping while there are still processes that have arrived but not yet finished running
        while (!D.isEmpty() || !Q.isEmpty()) {
            // Increment the current time by 1
            currentTime += 1;
            // Move all processes from the arrival queue to the priority queue if they have arrived by the current time
            while (!D.isEmpty()) {
                // Get the process with the earliest arrival time
                Process p = D.min().getValue();
                // If the process has arrived by the current time, move it to the priority queue and update its attributes
                if (p.getArrivalTime() <= currentTime) {
                    Q.insert(p.getPriority(), p);
                    p.setLastRunTime(currentTime - 1);
                    p.setWaitTime(0);
                    D.removeMin();
                    // else the process hasn't arrived yet, break out of the loop since the remaining processes haven't arrived yet either
                } else break;

            }

            // if !Q is empty
            if (!Q.isEmpty()) {
                // Get the process with the highest priority from the priority queue
                Process nextProcess = Q.min().getValue();
                // If the next process to run is different from the currently running process, update the output file
                if (nextProcess != runningP) {
                    // Print information about the next process to run
                    writer.println("Now Running Process Id = " + nextProcess.getProcessId());
                    writer.println("Arrival =" + nextProcess.getArrivalTime());
                    writer.println("Duration =" + nextProcess.getDuration());
                    writer.println(" Run time Left =" + nextProcess.getRunTimeLeft());
                    writer.println(" at time " + currentTime);
                    // If this is the first time the process is being run, record its start time
                    if (nextProcess.getStartTime() == -1) {
                        nextProcess.setStartTime(currentTime);
                    }
                }
                // Set the current process as the next process to run
                runningP = nextProcess;
                // Decrease the remaining time of the running process by 1
                runningP.setRunTimeLeft(runningP.getRunTimeLeft() - 1);
                // Set the last run time of the process to the current time
                runningP.setLastRunTime(currentTime);
                // Print information about the process being executed and its remaining time
                writer.println("Executed process ID:" + runningP.getProcessId() + ", at time " + currentTime + " Remaining: " + runningP.getRunTimeLeft());
                // Check if the running process has finished executing
                if (runningP.getRunTimeLeft() == 0) {
                    // Print information about the finished process
                    writer.println("Finished running Process id= " + nextProcess.getProcessId());
                    writer.println("Arrival =" + nextProcess.getArrivalTime());
                    writer.println("Duration =" + nextProcess.getDuration());
                    writer.println(" Run time Left =" + nextProcess.getRunTimeLeft());
                    writer.println(" at time " + currentTime);
                    // Remove the finished process from the priority queue
                    Q.removeMin();
                }
                // Create an iterator for the queue Q
                var iter = Q.iterator();
                // Iterate over the queue Q
                while (iter.hasNext()) {

                    // Get the next entry in the queue
                    Entry<Integer, Process> processEntry = iter.next();
                    // Check if the process is not the one currently running
                    if (runningP != processEntry.getValue()) {
                        // Increment the wait time of the process
                        processEntry.getValue().setWaitTime(processEntry.getValue().getWaitTime() + 1);
                        // Check if the process has exceeded the maximum wait time and reduce its priority if it has
                        if (processEntry.getValue().getWaitTime() > 0 && (processEntry.getValue().getWaitTime() % maxWaitTime) == 0) {
                            processEntry.getValue().setPriority(processEntry.getValue().getPriority() - 1);
                            writer.println("Process " + processEntry.getValue().getProcessId() + " reached maximum wait time ... decreasing priority to " + processEntry.getValue().getPriority());
                        }
                        // Replace the priority of the process in the queue with the updated priority
                        Q.replaceKey(processEntry, processEntry.getValue().getPriority());
                    }
                }

            }

        }
        // Print the message to indicate that all processes have finished running at current time
        writer.println("Finished running all processes at time " + currentTime + "\n");
        // Calculate the average wait time for all processes
        double averageWaitTime = allProcesses.stream().mapToInt(o -> o.getWaitTime()).average().getAsDouble();
        // Print the average wait time
        writer.print("Average wait time = " + averageWaitTime);
        // Close the writer
        writer.close();
    }


}