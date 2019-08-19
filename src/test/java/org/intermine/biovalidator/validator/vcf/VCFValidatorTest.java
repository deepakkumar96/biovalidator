package org.intermine.biovalidator.validator.vcf;

import org.intermine.biovalidator.BaseValidatorTest;
import org.intermine.biovalidator.api.ValidationResult;
import org.intermine.biovalidator.api.Validator;
import org.intermine.biovalidator.api.ValidatorBuilder;
import org.intermine.biovalidator.api.ValidatorHelper;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;


public class VCFValidatorTest extends BaseValidatorTest {

    private String mediumSizedFile = "/home/deepak/Documents/Intermine/FILES/VCF/mus_musculus_structural_variations.vcf";
    private String bigSizedFile = "/home/deepak/Documents/Intermine/FILES/VCF/danio_rerio_incl_consequences.vcf";

    @Ignore
    @Test
    public void testVCF() {
        simpleBenchmark(() -> {
            String filename = "/home/deepak/Documents/Intermine/FILES/VCF/CEU.low_coverage.2010_09.sites_SAMPLE.vcf";
            Validator validator = ValidatorBuilder.ofType(new VCFValidator(new File(bigSizedFile)))
                    .build();
            ValidationResult result = validator.validate();
            if (result.isValid()) {
                System.out.println("Valid");
            } else {
                System.err.println("InValid");
            }
            if (result.isNotValid()) {
                System.err.println(result.getErrorMessage());
            }
        });
    }

    @Test
    public void testValidVCFFile() {
        String file = getFullPath("vcf/valid/CEU.low_coverage.2010_09.sites_SAMPLE.vcf");
        ValidationResult result = ValidatorHelper.validateVcf(file, true);
        assertTrue(result.isValid());
    }

    @Test
    public void testWrongNumberOfDataColumns() {
        String file = getFullPath("vcf/invalid/wrong_data_columns.vcf");
        ValidationResult result = ValidatorHelper.validate(file, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "wrong number of column at line 6";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testInvalidID() {
        String file = getFullPath("vcf/invalid/invalid_id.vcf");
        ValidationResult result = ValidatorHelper.validate(file, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "CHROM is either missing or contains whitespace at line 6";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testInvalidNonIntegerPOS() {
        String file = getFullPath("vcf/invalid/invalid_pos.vcf");
        ValidationResult result = ValidatorHelper.validate(file, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "POS must be a numeric value at line 6";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testInvalidUnsortedPOS() {
        String file = getFullPath("vcf/invalid/invalid_unsorted_pos.vcf");
        ValidationResult result = ValidatorHelper.validate(file, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "POS (Positions) must be sorted numerically, in increasing"
                + " order, but not sorted at line 8";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testInvalidQuality() {
        String file = getFullPath("vcf/invalid/invalid_quality.vcf");
        ValidationResult result = ValidatorHelper.validate(file, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "QUAL can either be missing value '.' or a numeric value at line 6";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testWrongREF() {
        String file = getFullPath("vcf/invalid/invalid_REF.vcf");
        ValidationResult result = ValidatorHelper.validate(file, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "REF base is required and must be one of A,C,G,T,N " +
                "(case insensitive), and cannot contains whitespace(s) at line 6";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testWrongALTWithWhitespace() {
        String file = getFullPath("vcf/invalid/invalid_ALT.vcf");
        ValidationResult result = ValidatorHelper.validate(file, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "alternate base(ALT) has a whitespace at line 6";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testInvalidALTChars() {
        String file = getFullPath("vcf/invalid/invalid_ALT_chars.vcf");
        ValidationResult result = ValidatorHelper.validate(file, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "ALT can be either angle-bracket string or can only contain " +
                "one of A,C,G,T,N,* at line 6";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testFilterDoesNotContainWhitespace() {
        String file = getFullPath("vcf/invalid/invalid_filter.vcf");
        ValidationResult result = ValidatorHelper.validate(file, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "FILTER must not contain any whitespace, at line 6";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testInfoDoesNotContainWhitespace() {
        String file = getFullPath("vcf/invalid/invalid_info.vcf");
        ValidationResult result = ValidatorHelper.validateVcf(file, true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "INFO must not contain any whitespace, at line 6";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }
}
