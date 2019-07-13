package org.intermine.biovalidator.validator.gff3;

import org.intermine.biovalidator.api.ValidationFailureException;
import org.intermine.biovalidator.api.ValidationResult;
import org.intermine.biovalidator.api.ValidatorHelper;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Ignore
public class Gff3PerformanceTest {

    @Test
    public void test1GBGff3File() {
        simpleBenchmark(() -> {
            try {
                String file = "/home/deepak/Documents/FASTA_FILES/ref_GRCh38.p12_top_level (1).gff3";
                ValidationResult result = ValidatorHelper.validateGff3(file);
                if (result.isValid()) {
                    System.out.println("Valid File");
                } else {
                    System.out.println("Invalid File!\n" +result.getErrorMessage());
                    result.getErrorMessages().forEach(System.out::println);
                }
            } catch (ValidationFailureException e) {
                e.printStackTrace();
            }
        });
    }

    private static void simpleBenchmark(Runnable codeToBeValidated){
        long start = System.nanoTime();
        long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        codeToBeValidated.run();

        long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        long timeTaken = System.nanoTime() - start;
        System.out.println("Time Taken : " +
                TimeUnit.MILLISECONDS.convert((timeTaken), TimeUnit.NANOSECONDS));
        System.out.print("Memory Used : " + (afterUsedMem-beforeUsedMem));
    }
}