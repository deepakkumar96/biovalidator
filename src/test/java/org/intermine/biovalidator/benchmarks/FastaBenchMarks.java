package org.intermine.biovalidator.benchmarks;

import org.intermine.biovalidator.api.ValidationResult;
import org.intermine.biovalidator.api.ValidatorHelper;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import static junit.framework.TestCase.assertTrue;
import static org.intermine.biovalidator.validator.gff3.Gff3PerformanceTest.simpleBenchmark;

public class FastaBenchMarks {

   // @Test
    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void withApprox100MBFile() {
        String filename = "/home/deepak/Documents/Intermine/benchmark_files/fasta/Homo_sapiens.GRCh38.dna_rm.chromosome.14_100mb.fa";
        ValidationResult result = ValidatorHelper.validate(filename, "fasta", true);
        assertTrue(result.isValid());
    }

    @Test
    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void withApprox1GBFile() {
        simpleBenchmark(() -> {
            String filename = "/home/deepak/Downloads/FASTA/Mus_musculus.GRCm38.dna.toplevel.fa";
            ValidationResult result = ValidatorHelper.validate(filename, "fasta", true);
            assertTrue(result.isValid());
        });
    }

    //@Benchmark
    //@BenchmarkMode(Mode.SingleShotTime)
    public void withApprox5GBFile() {
        String filename = "/home/deepak/Documents/FASTA_FILES/protein.fa";
        ValidationResult result = ValidatorHelper.validate(filename, "fasta", true);
        assertTrue(result.isValid());
    }
}
