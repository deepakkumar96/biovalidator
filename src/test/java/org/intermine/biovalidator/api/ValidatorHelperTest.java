package org.intermine.biovalidator.api;

import org.intermine.biovalidator.validator.ValidatorType;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class ValidatorHelperTest {

    private String filename = "src/test/resources/fasta/valid/dna_sample.fa";

    @Test
    public void testValidatorHelper() {
        ValidationResult result = ValidatorHelper.validateFasta(filename);
        if (!result.isValid()) {
            result.getErrorMessages().forEach(System.out::println);
        }
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void testValidatorHelperWithDirectValidatorType() {
        ValidationResult result = ValidatorHelper.validateFastaDna(filename);
        if (!result.isValid()) {
            result.getErrorMessages().forEach(System.out::println);
        }
        Assert.assertTrue(result.isValid());
    }

    @Test
    public void testCatchExceptionIfThrown() {
        ValidationResult result = ValidatorHelper.validateFasta("/invalid_random_file");
        assertTrue(result.isNotValid());
        assertEquals(result.getErrorMessage(), "/invalid_random_file (No such file or directory)");
    }

    @Test
    public void testWrongValidatorType() {
        ValidationResult result = ValidatorHelper.validate("src/test/resources/fasta/fasta_rules",
                "fastaQ", true);
        assertTrue(result.isNotValid());
        String errMsg = "Missing or Invalid Validator type! It must be one of ("
                + Arrays.toString(ValidatorType.values()) +"), case-insensitive.";
        assertEquals(errMsg, result.getErrorMessage());
    }
}
