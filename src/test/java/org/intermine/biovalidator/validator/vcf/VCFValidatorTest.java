package org.intermine.biovalidator.validator.vcf;

import org.intermine.biovalidator.BaseValidatorTest;
import org.intermine.biovalidator.api.Parser;
import org.intermine.biovalidator.api.ValidationResult;
import org.intermine.biovalidator.api.Validator;
import org.intermine.biovalidator.api.ValidatorBuilder;
import org.intermine.biovalidator.parser.GenericLineByLineParser;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;

@Ignore
public class VCFValidatorTest extends BaseValidatorTest {

    String mediumSizedFile = "/home/deepak/Documents/Intermine/FILES/VCF/mus_musculus_structural_variations.vcf";
    String bigSizedFile = "/home/deepak/Documents/Intermine/FILES/VCF/danio_rerio_incl_consequences.vcf";

    @Test
    public void testVCF() {
        simpleBenchmark(() -> {
            String filename = "/home/deepak/Documents/Intermine/FILES/VCF/CEU.low_coverage.2010_09.sites_SAMPLE.vcf";
            Validator validator = ValidatorBuilder.ofType(new VCFValidator(bigSizedFile))
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
    public void testVCFParser() {
        simpleBenchmark(() -> {
            try {
                Parser<String> parser = new GenericLineByLineParser(new FileReader(bigSizedFile));
                String line = parser.parseNext();
                long currentLine = 1;
                while (line != null) {
                    line = parser.parseNext();
                    if (currentLine == 17262845L) {
                        System.out.println(line);
                        break;
                    }
                    currentLine++;
                }
                System.out.println("Total Line : " + currentLine);
                /*VCFFileReader reader = new VCFFileReader(new File(bigSizedFile), false);
                reader.forEach(line -> {

                });*/
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        String filename = "/home/deepak/Documents/Intermine/FILES/VCF/CEU.low_coverage.2010_09.sites_SAMPLE.vcf";
        Validator validator = ValidatorBuilder.ofType(new VCFValidator(filename))
                .build();
        ValidationResult result = validator.validate();
        if (result.isValid()) {
            System.out.println("Valid");
        } else {
            System.out.println("InValid");
        }
        System.out.println(result.getErrorMessage());
    }
}
