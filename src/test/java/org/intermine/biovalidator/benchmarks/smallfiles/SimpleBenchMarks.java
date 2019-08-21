package org.intermine.biovalidator.benchmarks.smallfiles;

import org.intermine.biovalidator.api.ValidationResult;
import org.intermine.biovalidator.api.ValidatorHelper;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import static junit.framework.TestCase.assertTrue;

public class SimpleBenchMarks {

    @Test
    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    public void smallFastaFile() {
        String filename = "/home/deepak/Documents/FASTA_FILES/protein.fa";
        ValidationResult result = ValidatorHelper.validate(filename, "fasta", true);
        assertTrue(result.isValid());
    }
}
