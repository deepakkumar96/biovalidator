package org.intermine.biovalidator.validator.vcf;

import org.intermine.biovalidator.BaseValidatorTest;
import org.intermine.biovalidator.api.ValidationResult;
import org.intermine.biovalidator.api.ValidatorHelper;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class VcfHeaderTest extends BaseValidatorTest {

    @Test
    public void tesValidVcf() {
        String path = getFullPath("vcf/valid/CEU.low_coverage.2010_09.sites_SAMPLE.vcf");
        ValidationResult result = ValidatorHelper.validate(path, "", true);
        assertTrue(result.isValid());
    }

    @Test
    public void tesMissingMetaAngleBrackets() {
        String path = getFullPath("vcf/invalid/CEU.low_coverage.2010_09.sites_SAMPLE.vcf");
        ValidationResult result = ValidatorHelper.validate(path, "", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "A META header value must be wrapped inside angle brackets (<...>)"
                + " at line 4";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void tesMetaHeaderNotInKeyValuePair() {
        String path = getFullPath("vcf/invalid/sample_meta_header_not_in_key_value_pair.vcf");
        ValidationResult result = ValidatorHelper.validate(path, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "A META header must be a key-value pair at line 3";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testMissingInfoMetaData() {
        String path = getFullPath("vcf/invalid/sample_info_meta_missing_type.vcf");
        ValidationResult result = ValidatorHelper.validate(path, "vcf", true);
        assertTrue(result.isNotValid());
        String expectedErrorMsg = "Your input file has a malformed header: Unexpected tag  " +
                "in line <ID=DP,Number=1,=Integer>";
        assertEquals(expectedErrorMsg, result.getErrorMessage());
    }

    @Test
    public void testMissingVCFVersion() {
        String path = getFullPath("vcf/invalid/invalid_vcf_file.vcf");
        ValidationResult result = ValidatorHelper.validate(path, "vcf", true);
        assertTrue(result.isNotValid());
        String errorMsg = "First line must be a VCF header line";
        assertEquals(errorMsg, result.getErrorMessage());
    }

    @Test
    public void testMissingVCFContentHeader() {
        String path = getFullPath("vcf/invalid/missing_content_header.vcf");
        ValidationResult result = ValidatorHelper.validate(path, "vcf", true);
        assertTrue(result.isNotValid());
        String errorMsg = "Your input file has a malformed header: We never saw the required"
                + " CHROM header line (starting with one #) for the input VCF file";
        assertEquals(errorMsg, result.getErrorMessage());
    }
}