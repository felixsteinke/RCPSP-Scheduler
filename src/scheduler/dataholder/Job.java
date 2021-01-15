package scheduler.dataholder;/*
 * Einlese-Programm wurde von Studierende der HFT Stuttgart entwickelt
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Job {

    // Number of a job
    public int nummer;

    public int vorgaengerFlag;

    //Zeitpunkt des starts
    public Integer start = null;

    //zeitpunkt des endes
    public Integer ende = null;

    // successors; each element contains the job-number (int)
    public ArrayList<Integer> nachfolger;

    // predecessors; each element contains the job-number (int)
    public ArrayList<Integer> vorgaenger;

    // duration of a job
    public int dauer;

    // needed resource capacities
    // verwendeteResourcen[0] --> capacities of resource R1
    // verwendeteResourcen[1] --> capacities of resource R2
    // verwendeteResourcen[2] --> capacities of resource R3
    // verwendeteResourcen[3] --> capacities of resource R4
    int[] verwendeteResourcen;


    public Job(int nummer, ArrayList<Integer> nachfolger, int dauer, int[] verwendeteResourcen) {
        this.nummer = nummer;
        this.nachfolger = nachfolger;
        this.dauer = dauer;
        this.verwendeteResourcen = verwendeteResourcen;
        this.vorgaenger = new ArrayList<Integer>();
    }

    public static Job getJob(Job[] jobs, int nummer) {
        Job j = null;
        for (Job job : jobs) {
            if (nummer == job.nummer) {
                j = job;
            }
        }
        return j;
    }

    public static Job[] read(File file) throws FileNotFoundException {

        Scanner scanner = new Scanner(file);
        Job[] jobs = new Job[0];
        int index = 0;

        ArrayList<ArrayList<Integer>> successors = new ArrayList<ArrayList<Integer>>();
        boolean startJob = false;

        while (scanner.hasNext()) {
            String nextLine = scanner.nextLine();
            if (nextLine.equals("")) {
                continue;
            }
            Scanner lineScanner = new Scanner(nextLine);
            String nextString = lineScanner.next();

            if (nextString.equals("jobs")) {
                boolean found = false;
                while (!found) {
                    if (lineScanner.next().equals("):")) {
                        int length = lineScanner.nextInt();
                        jobs = new Job[length];
                        for (int i = 0; i < jobs.length; i++) {
                            successors.add(new ArrayList<Integer>());
                        }
                        found = true;
                    }
                }
                continue;
            }
            if (nextString.equals("jobnr.")) {
                startJob = true;
            }
            if (startJob) {
                try {
                    lineScanner.next();
                    if (lineScanner.hasNext()) {
                        lineScanner.next();
                        while (lineScanner.hasNext()) {
                            int suc = Integer.parseInt(lineScanner.next());
                            successors.get(index).add(suc);
                        }
                        index++;
                        if (index == jobs.length) {
                            break;
                        }
                    }
                } catch (NumberFormatException ignored) {
                }
            }

        }
        index = 0;
        boolean startRequests = false;
        scanner = new Scanner(file);
        while (scanner.hasNext()) {
            String next = scanner.nextLine();

            if (next.equals("")) {
                continue;
            }
            Scanner lineScanner = new Scanner(next);
            String nextString = lineScanner.next();
            if (!startRequests && lineScanner.hasNext()) {
                if (lineScanner.next().equals("mode")) {
                    startRequests = true;
                }
            }
            if (startRequests) {
                try {
                    int nummer = Integer.parseInt(nextString);
                    nextString = lineScanner.next();
                    int[] res = new int[4];
                    if (lineScanner.hasNext()) {
                        int duration = Integer.parseInt(lineScanner.next());
                        if (lineScanner.hasNext()) {
                            nextString = lineScanner.next();
                            res[0] = Integer.parseInt(nextString);
                            if (lineScanner.hasNext()) {
                                nextString = lineScanner.next();
                                res[1] = Integer.parseInt(nextString);
                                if (lineScanner.hasNext()) {
                                    nextString = lineScanner.next();
                                    res[2] = Integer.parseInt(nextString);
                                    if (lineScanner.hasNext()) {
                                        nextString = lineScanner.next();
                                        res[3] = Integer.parseInt(nextString);
                                    }
                                }
                            }

                        }

                        jobs[index] = new Job(nummer, successors.get(index), duration, res);
                        index++;
                        if (index == jobs.length) {
                            break;
                        }
                    }

                } catch (NumberFormatException ignored) {
                }
            }

        }
        return jobs;
    }

    public String toString() {
        return "Objects.Job: " + nummer + ", flag: " + vorgaengerFlag + ", start: " + start + ", ende: " + ende + ", vorgänger: " + Arrays.toString(vorgaenger.toArray())
                + ", Res: " + Arrays.toString(verwendeteResourcen);
    }

    public int nummer() {
        return nummer;
    }

    public ArrayList<Integer> nachfolger() {
        return nachfolger;
    }

    public ArrayList<Integer> vorgaenger() {
        return vorgaenger;
    }

    public int dauer() {
        return dauer;
    }

    public int verwendeteResource(int i) {
        if (i >= 0 && i <= 3)
            return verwendeteResourcen[i];
        else
            throw new IllegalArgumentException("Parameter muss zwischen 0 und 3 sein!");
    }

    public int anzahlNachfolger() {
        return nachfolger.size();
    }

    public void calculatePredecessors(Job[] jobs) {
        for (Job job : jobs) {
            for (int nachfolger : job.nachfolger) {
                if (nachfolger == this.nummer) {
                    this.vorgaenger().add(job.nummer);
                }
            }
            job.vorgaengerFlag = job.vorgaenger.size();
        }

    }
}
