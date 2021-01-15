package scheduler.algorithm;

import scheduler.dataholder.Job;
import scheduler.dataholder.Resource;
import scheduler.graphic.Gui;

import java.io.*;
import java.util.ArrayList;

public class Algorithm {

    private final BufferedWriter bwMakespan = new BufferedWriter(new FileWriter(new File("output/makespan.csv")));
    private final BufferedWriter bwResult = new BufferedWriter(new FileWriter(new File("output/result.txt")));
    private Job[] jobs;
    private Resource[] resources;
    private int horizon = 0;
    private int[][] res;
    private int res1;
    private int res2;
    private int res3;
    private int res4;
    private ArrayList<Integer> planbar = new ArrayList<>();
    private ArrayList<Integer> result = new ArrayList<>();

    public Algorithm() throws IOException {
    }

    public void doMultipleShift(String filename) throws FileNotFoundException {
        setup(filename);
        alg();
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                new Shift(jobs, resources, res, result, jobs[result.get(result.size() - 1) - 1].ende).run(true);
                continue;
            }
            new Shift(jobs, resources, res, result, jobs[result.get(result.size() - 1) - 1].ende).run(false);
        }
        printResult(res, jobs[result.get(result.size() - 1) - 1].ende);
    }

    public void doOne(String filename) throws IOException {
        setup(filename);
        alg();
        int[][] resNeu = new int[horizon][4];
        for (int c = 0; c < res.length; c++) {
            for (int i = 0; i < res[0].length; i++) {
                resNeu[c][i] = res[c][i];
            }
        }
        int dauer = jobs[result.get(result.size() - 1) - 1].ende;
        //printResult(resNeu, dauer);

        Shift s = new Shift(jobs, resources, res, result, jobs[result.get(result.size() - 1) - 1].ende);
        s.run(true);
        s.dispResult();

        writeResult("result", "input/j1201_1.sm", 0);
    }

    public void doAll() throws IOException {
        for (int i = 1; i <= 60; i++) {
            for (int j = 1; j <= 10; j++) {
                String name = "input/j120" + i + "_" + j + ".sm";
                setup(name);
                alg();

                int makespan = jobs[result.get(result.size() - 1) - 1].ende;

                Shift shift = new Shift(jobs, resources, res, result, jobs[result.get(result.size() - 1) - 1].ende);
                shift.run(true);
                writeResult("makespan", name, makespan);
                writeResult("result", name, 0);
            }
        }
    }

    private void setup(String filename) throws FileNotFoundException {
        jobs = Job.read(new File(filename));
        resources = Resource.read(new File(filename));
        result = new ArrayList<>();
        planbar = new ArrayList<>();
        horizon = 0;
        for (Job job : jobs) {
            job.calculatePredecessors(jobs);
            horizon += job.dauer;
        }
        res = new int[horizon][4];
        res1 = resources[0].maxVerfuegbarkeit;
        res2 = resources[1].maxVerfuegbarkeit;
        res3 = resources[2].maxVerfuegbarkeit;
        res4 = resources[3].maxVerfuegbarkeit;
        for (int i = 0; i < horizon; i++) {
            res[i][0] = res1;
            res[i][1] = res2;
            res[i][2] = res3;
            res[i][3] = res4;
        }
        jobs[0].start = 0;
        jobs[0].ende = 0;
        updatePool(jobs[0]);
    }

    private void alg() {
        while (!planbar.isEmpty()) {
            planbar.sort((e1, e2) -> Integer.compare(jobs[e2 - 1].nachfolger.size(), jobs[e1 - 1].nachfolger.size()));
            Integer start = fruehesterZeitpunkt(jobs[planbar.get(0) - 1]);
            if (start == null) {
                throw new IllegalArgumentException("Element nicht erlaubt im Pool");
            }
            while (!isPlatz(jobs[planbar.get(0) - 1], start)) {
                start++;
            }
            einplanen(jobs[planbar.get(0) - 1], start);
        }
    }

    private void printResult(int[][] res, int dauer) {
        System.out.println("Dauer: " + jobs[result.get(result.size() - 1) - 1].ende);
        new Gui(dauer, res, false);
    }

    private void updatePool(Job job) {
        if (job.nummer != 1) {
            for (int i = 0; i < planbar.size(); i++) {
                if (planbar.get(i) == job.nummer) {
                    planbar.remove(i);
                    break;
                }
            }
        }
        for (int i = 0; i < job.nachfolger.size(); i++) {
            jobs[job.nachfolger.get(i) - 1].vorgaengerFlag--;
            if (jobs[job.nachfolger.get(i) - 1].vorgaengerFlag == 0) {
                planbar.add(job.nachfolger.get(i));
            }
        }
    }

    private void einplanen(Job job, int startzeit) {
        for (int i = 0; i < job.dauer; i++) {
            res[startzeit + i][0] -= job.verwendeteResource(0);
            res[startzeit + i][1] -= job.verwendeteResource(1);
            res[startzeit + i][2] -= job.verwendeteResource(2);
            res[startzeit + i][3] -= job.verwendeteResource(3);
        }
        job.start = startzeit;
        job.ende = startzeit + job.dauer;
        result.add(job.nummer);
        updatePool(job);

    }

    private boolean isPlatz(Job job, int startzeit) {
        int platz = 0;
        for (int i = 0; i < job.dauer; i++) {
            if (res[startzeit + i][0] > job.verwendeteResource(0) &&
                    res[startzeit + i][1] > job.verwendeteResource(1) &&
                    res[startzeit + i][2] > job.verwendeteResource(2) &&
                    res[startzeit + i][3] > job.verwendeteResource(3)) {
                platz++;
            }
        }
        return platz == job.dauer;
    }

    private Integer fruehesterZeitpunkt(Job job) {
        int min = 0;
        for (int i = 0; i < job.vorgaenger.size(); i++) {
            if (jobs[job.vorgaenger.get(i) - 1].ende == null) {
                return null;
            }
            min = Math.max(min, jobs[job.vorgaenger.get(i) - 1].ende);
        }
        return min;
    }

    private void writeResult(String mode, String srcName, int value) throws IOException {
        switch (mode) {
            case "makespan":
                bwMakespan.write(srcName + "," + value + "," + jobs[result.get(result.size() - 1) - 1].ende);
                bwMakespan.newLine();
                bwMakespan.flush();
                break;
            case "result":
                bwResult.write(srcName);
                bwResult.newLine();
                for (Job job : jobs) {
                    bwResult.write(job.nummer + "\t");
                    bwResult.flush();
                }
                bwResult.newLine();
                for (Job job : jobs) {
                    bwResult.write(job.start + "\t");
                    bwResult.flush();
                }
                bwResult.newLine();
                bwResult.flush();
                break;
        }
    }

    public int getHorizon() {
        return horizon;
    }

    public int getRes(int num) {
        switch (num) {
            case 0:
                return res1;
            case 1:
                return res2;
            case 2:
                return res3;
            case 3:
                return res4;
            default:
                throw new IllegalArgumentException("Arg has to be between 0 and 3!");
        }
    }

    public void auslesen(Job[] jobs) {
        int gesamtDauer = 0;
        for (Job job : jobs) {
            gesamtDauer += job.dauer();

            System.out.print("Nummer: " + job.nummer() + "     |    ");
            System.out.print("Nachfolger: ");
            ArrayList<Integer> nachfolger = job.nachfolger();
            for (Integer value : nachfolger) {
                System.out.print(" " + value + " ");

            }
            System.out.print(" Vorgaenger: ");
            ArrayList<Integer> vorgaenger = job.vorgaenger();
            vorgaenger.stream().map(integer -> " " + integer + " ").forEach(System.out::print);
            System.out.print("     |    ");
            System.out.print("Dauer: " + job.dauer() + "     |    ");
            System.out.println("R1: " + job.verwendeteResource(0) + "  R2: " + job.verwendeteResource(1) +
                    "  R3: " + job.verwendeteResource(2) + "  R4: " + job.verwendeteResource(3));
        }
        System.out.println("T = " + gesamtDauer);
    }

    public void auslesen(Resource[] resource) {
        for (Resource value : resource) {
            System.out.print("Objects.Resource: " + value.nummer() + "     |    ");
            System.out.println("Verfuegbarkeit: " + value.maxVerfuegbarkeit());
        }
    }
}
