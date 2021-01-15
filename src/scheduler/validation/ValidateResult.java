package scheduler.validation;

import scheduler.dataholder.Job;
import scheduler.dataholder.Resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ValidateResult {

    private static Job[] jobs;
    private static Resource[] resources;

    private ValidateResult() {
    }

    public static void validate() throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File("output/result.txt")));
        for (int i = 0; i < 600; i++) {
            String file = br.readLine();
            String[] nummern = br.readLine().split("\t");
            String[] starts = br.readLine().split("\t");

            jobs = Job.read(new File(file));
            resources = Resource.read(new File(file));

            for (int k = 0; k < nummern.length; k++) {
                jobs[Integer.parseInt(nummern[k]) - 1].start = Integer.valueOf(starts[k]);
            }
            int[][] res = new int[jobs[jobs.length - 1].start][4];
            for (Job job : jobs) {
                long errorCount = job.nachfolger.stream().filter(n -> jobs[n - 1].start < job.start).count();
                if (errorCount > 0) {
                    throw new Exception("Illegal schedule");
                }
                for (int k = 0; k < job.dauer; k++) {
                    res[job.start + k][0] += job.verwendeteResource(0);
                    res[job.start + k][1] += job.verwendeteResource(1);
                    res[job.start + k][2] += job.verwendeteResource(2);
                    res[job.start + k][3] += job.verwendeteResource(3);
                }
            }
            for (int k = 0; k < res.length; k++) {
                if (res[k][0] > resources[0].maxVerfuegbarkeit ||
                        res[k][1] > resources[1].maxVerfuegbarkeit ||
                        res[k][2] > resources[2].maxVerfuegbarkeit ||
                        res[k][3] > resources[3].maxVerfuegbarkeit) {
                    System.out.println("Validation of file " + file + " failed");
                    return;
                }
            }
            System.out.println("Validation of file " + file + " was successful");
        }
    }
}
