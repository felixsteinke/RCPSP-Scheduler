package scheduler;

import scheduler.algorithm.Algorithm;
import scheduler.validation.ValidateResult;

public class Main {

    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        new Algorithm().doAll(/*"input/j1201_1.sm"*/);
        long end = System.nanoTime();
        System.out.println("Done in " + (end - start) + "ms.");
        ValidateResult.validate();
    }
}
