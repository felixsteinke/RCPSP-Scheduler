package scheduler.algorithm;

import scheduler.dataholder.Job;
import scheduler.dataholder.Resource;
import scheduler.graphic.Gui;

import java.util.ArrayList;

public class Shift {

    private final Job[] jobs;
    private final Resource[] resources;
    private final int[][] res;
    private final ArrayList<Job> result = new ArrayList<>();
    private final int duration;
    private int latestFinishCurrJob;
    private int earliestStartCurrJob = 0;

    public Shift(Job[] jobs, Resource[] resources, int[][] res, ArrayList<Integer> result, int duration) {
        this.jobs = jobs;
        this.resources = resources;
        this.res = res;
        result.forEach((e) -> this.result.add(jobs[e - 1]));
        this.duration = duration;
        this.latestFinishCurrJob = duration;
    }

    public void dispResult() {
        System.out.println("Dauer nach shift: " + (result.get(result.size() - 1)).ende);
        new Gui((result.get(result.size() - 1)).ende, res, true);
    }

    public int run(boolean direction) {
        if (direction) {
            result.sort((a, b) -> -1);
            for (Job currJob : result) {
                currJob.nachfolger.forEach(n -> latestFinishCurrJob = Math.min(latestFinishCurrJob, jobs[n - 1].start));
                int latestStart = latestFinishCurrJob - currJob.dauer;
                releaseJob(currJob);
                while (!checkRes(currJob, latestStart)) {
                    latestStart--;
                }
                rebaseJob(currJob, latestStart);
                latestFinishCurrJob = duration;
            }
        } else {
            for (Job currJob : result) {
                currJob.vorgaenger.forEach(n -> earliestStartCurrJob = Math.max(earliestStartCurrJob, jobs[n - 1].ende));
                releaseJob(currJob);
                while (!checkRes(currJob, earliestStartCurrJob)) {
                    earliestStartCurrJob++;
                }
                rebaseJob(currJob, earliestStartCurrJob);
                earliestStartCurrJob = 0;
            }
        }
        result.sort((a, b) -> a.start.compareTo(b.start));
        this.trim();
        return result.get(result.size() - 1).ende;
    }

    private void trim() {
        int start = (result.get(0)).start;
        result.forEach((e) -> {
            e.start = e.start - start;
            e.ende = e.ende - start;
        });
        for (int i = 0; i < (result.get(result.size() - 1)).ende; i++) {
            res[i][0] = res[i + start][0];
            res[i][1] = res[i + start][1];
            res[i][2] = res[i + start][2];
            res[i][3] = res[i + start][3];
        }
    }

    private boolean checkRes(Job job, int instant) {
        if (instant < 0)
            throw new IllegalArgumentException("\n\tIndex less than 0.\n\t" + job.toString() + "\n\tinstant:" + instant);
        for (int i = 0; i < job.dauer; i++) {
            if (res[instant + i][0] < job.verwendeteResource(0) ||
                    res[instant + i][1] < job.verwendeteResource(1) ||
                    res[instant + i][2] < job.verwendeteResource(2) ||
                    res[instant + i][3] < job.verwendeteResource(3)) {
                return false;
            }
        }
        return true;
    }

    private void rebaseJob(Job job, int newStart) {
        job.start = newStart;
        job.ende = newStart + job.dauer;
        for (int i = 0; i < job.dauer; i++) {
            res[job.start + i][0] -= job.verwendeteResource(0);
            res[job.start + i][1] -= job.verwendeteResource(1);
            res[job.start + i][2] -= job.verwendeteResource(2);
            res[job.start + i][3] -= job.verwendeteResource(3);
        }
    }

    private void releaseJob(Job job) {
        for (int i = 0; i < job.dauer; i++) {
            res[job.start + i][0] += job.verwendeteResource(0);
            res[job.start + i][1] += job.verwendeteResource(1);
            res[job.start + i][2] += job.verwendeteResource(2);
            res[job.start + i][3] += job.verwendeteResource(3);
        }

    }
}
