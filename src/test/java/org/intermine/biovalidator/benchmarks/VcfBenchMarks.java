package org.intermine.biovalidator.benchmarks;

import org.intermine.biovalidator.api.ValidationResult;
import org.intermine.biovalidator.api.ValidatorHelper;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import static junit.framework.TestCase.assertTrue;

public class VcfBenchMarks {

    /*@Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void withApprox100MBFile() {
        String filename = "/home/deepak/Documents/FASTA_FILES/protein.fa";
        ValidationResult result = ValidatorHelper.validate(filename, "fasta", true);
        assertTrue(result.isValid());
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void withApprox1GBFile() {
        String filename = "/home/deepak/Documents/FASTA_FILES/protein.fa";
        ValidationResult result = ValidatorHelper.validate(filename, "fasta", true);
        assertTrue(result.isValid());
    }

    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void withApprox5GBFile() {
        String filename = "/home/deepak/Documents/FASTA_FILES/protein.fa";
        ValidationResult result = ValidatorHelper.validate(filename, "fasta", true);
        assertTrue(result.isValid());
    }*/
}
