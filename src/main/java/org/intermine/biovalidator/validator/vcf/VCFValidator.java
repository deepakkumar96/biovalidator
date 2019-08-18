package org.intermine.biovalidator.validator.vcf;

/*
 * Copyright (C) 2002-2019 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import htsjdk.samtools.seekablestream.SeekableFileStream;
import htsjdk.variant.utils.VCFHeaderReader;
import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFHeader;
import org.apache.commons.lang3.StringUtils;
import org.intermine.biovalidator.api.ErrorMessage;
import org.intermine.biovalidator.api.Parser;
import org.intermine.biovalidator.api.ValidationResult;
import org.intermine.biovalidator.parser.GenericLineByLineParser;
import org.intermine.biovalidator.parser.vcf.VCFDataLine;
import org.intermine.biovalidator.parser.vcf.VCFDataParser;
import org.intermine.biovalidator.parser.vcf.VCFLine;
import org.intermine.biovalidator.utils.BioValidatorUtils;
import org.intermine.biovalidator.validator.AbstractValidator;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import static org.intermine.biovalidator.parser.vcf.VCFDataParser.VCF_HEADER_LINE;

/**
 * VCF validator
 * @author deepak kumar
 */
public class VCFValidator extends AbstractValidator
{
    private static final String HEADER_START = "##fileformat=VCFv";
    private static final String MISSING_VALUE = ".";
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s");

    private static final String[] META_HEADER = {VCFConstants.INFO_HEADER_START,
        VCFConstants.FILTER_HEADER_START, VCFConstants.FORMAT_HEADER_START,
        VCFConstants.CONTIG_HEADER_START,
        VCFConstants.ALT_HEADER_START, VCFConstants.PEDIGREE_HEADER_START
    };
    private static final char[] VALID_REF_VALUES = {'A', 'C', 'G', 'T', 'N' };
    private static final char[] VALID_ALT_VALUES = {'A', 'C', 'G', 'T', 'N', '*', ',' };

    private final Set<String> uniqueIDs;
    private final File fileToBeValidated;
    private VCFHeader header;
    private long lastReadPosFiledValue;

    /**
     * Construct a VCF validator with a filename
     * @param filename file to be validated
     */
    public VCFValidator(String filename) {
        fileToBeValidated = new File(filename);
        uniqueIDs = new HashSet<>();
        lastReadPosFiledValue = 0;
    }
    @Nonnull
    @Override
    public ValidationResult validate() {
        try ( InputStreamReader isr = new FileReader(fileToBeValidated);
              VCFDataParser vcfDataParser = new VCFDataParser(isr)) {
            verifyHeaderFormats();
            header = VCFHeaderReader.readHeaderFrom(new SeekableFileStream(fileToBeValidated));

            long currentLineNumber = vcfDataParser.getHeadersLinesCount();
            Optional<VCFLine> vcfLineOpt = vcfDataParser.parseNext();
            currentLineNumber++; // as first line is read above
            while (vcfLineOpt.isPresent()) {
                VCFDataLine vcfDataLine = (VCFDataLine) vcfLineOpt.get();
                validateVCFDataLine(vcfDataLine, currentLineNumber);
                if (validationResult.isNotValid()
                        && validationResultStrategy.shouldStopAtFirstError()) {
                    return validationResult;
                }
                vcfLineOpt = vcfDataParser.parseNext();
                currentLineNumber++;
            }
            System.out.println("Total Lines: " + currentLineNumber);
        } catch (IOException e) {
            validationResult.addError(ErrorMessage.of(e.getMessage()));
        }
        return validationResult;
    }

    private void validateVCFDataLine(VCFDataLine vcfDataLine, long currentLineNumber) {
        String chrom = vcfDataLine.getChrom();
        if (BioValidatorUtils.containsWhitespace(chrom)) {
            validationResult.addError("ID is either missing or contains whitespace at line "
                    + currentLineNumber);
            if (validationResultStrategy.shouldStopAtFirstError()) {
                return;
            }
        }
        //validating POS field
        if (BioValidatorUtils.isInteger(vcfDataLine.getPos())) {
            long currentLinePOSFieldValue = Integer.parseInt(vcfDataLine.getPos());
            if (currentLinePOSFieldValue < lastReadPosFiledValue) {
                String errMsg = "Positions must be sorted numerically, in increasing order,"
                        + " but not sorted at line " + currentLineNumber;
                //validationResult.addError(errMsg); // TODO whether to use this rule or not
                if (validationResultStrategy.shouldStopAtFirstError()) {
                    //return; //todo
                }
            }
            lastReadPosFiledValue = currentLinePOSFieldValue;
        } else {
            validationResult.addError("POS must be a numeric value at line " + currentLineNumber);
            if (validationResultStrategy.shouldStopAtFirstError()) {
                return;
            }
        }

        //validating ID
       /* if (!StringUtils.equals(vcfDataLine.getId(), MISSING_VALUE)) {
            boolean isAdded = uniqueIDs.add(vcfDataLine.getId());
            if (!isAdded) {
                validationResult.addError("Identifier(ID) is duplicated at line "
                        + currentLineNumber);
                if (validationResultStrategy.shouldStopAtFirstError()) {
                    return;
                }
            }
        }*/

        //validating REF
        if (!StringUtils.containsOnly(vcfDataLine.getRef(), VALID_REF_VALUES)) {
            validationResult.addError("REF base is required and must be one of A,C,G,T,N "
                   + "(case insensitive), but contains whitespace(s) at line " + currentLineNumber);
            if (validationResultStrategy.shouldStopAtFirstError()) {
                return;
            }
        }
        //validating ALT
        String alt = vcfDataLine.getAlt();
        if (!StringUtils.equals(alt, MISSING_VALUE)) {
            if (BioValidatorUtils.containsWhitespace(alt)) {
                validationResult.addError(ErrorMessage.of("alternate base(ALT) has a whitespace"
                        + " at line " + currentLineNumber));
                if (validationResultStrategy.shouldStopAtFirstError()) {
                    return;
                }
            }
            if (isNotAngleBracketWrappedString(alt)
                    && !StringUtils.containsOnly(alt, VALID_ALT_VALUES)) {
                validationResult.addError("ALT can be either angle-bracket string or"
                        + " can only contain one of A,C,G,T,N,* at line " + currentLineNumber);
                if (validationResultStrategy.shouldStopAtFirstError()) {
                    return;
                }
            }
        }

        // validating QUAL
        if (!StringUtils.equals(vcfDataLine.getQual(), MISSING_VALUE)
                && !BioValidatorUtils.isInteger(vcfDataLine.getQual())) {
            validationResult.addError("QUAL can either be missing value '.' or a numeric"
                    + " value at line " + currentLineNumber);
            if (validationResultStrategy.shouldStopAtFirstError()) {
                return;
            }
        }

        // validate FILTER
        if (BioValidatorUtils.containsWhitespace(vcfDataLine.getFilter())) {
            validationResult.addError("FILTER must not contain any"
                    + "whitespace, at line " + currentLineNumber);
            if (validationResultStrategy.shouldStopAtFirstError()) {
                return;
            }
        }

        //validate INFO
        if (BioValidatorUtils.containsWhitespace(vcfDataLine.getInfo())) {
            validationResult.addError("INFO must not contain any"
                    + "whitespace, at line " + currentLineNumber);
        }

        /*//validating ID
        if (!StringUtils.equals(vcfDataLine.getId(), MISSING_VALUE)) {
            boolean isAdded = uniqueIDs.add(vcfDataLine.getId());
            if (!isAdded) {
                validationResult.addError("Identifier(ID) is duplicated at line "
                        + currentLineNumber);
                if (validationResultStrategy.shouldStopAtFirstError()) {
                    return;
                }
            }
        }*/

    }

    private void verifyHeaderFormats() {
        try ( InputStreamReader isr = new FileReader(fileToBeValidated);
              Parser<String> lineParser = new GenericLineByLineParser(isr)) {
            String line = lineParser.parseNext();
            long currentLineNumber = 1;
            while (line != null && line.startsWith("#") && !line.startsWith(VCF_HEADER_LINE)) {
                if ( currentLineNumber == 1 && !line.startsWith(HEADER_START)) {
                    validationResult.addError("First line must be a VCF header line");
                    return;
                }
                if (StringUtils.startsWithAny(line, META_HEADER)) {
                    checkIsWrappedWithinAngleBracketed(line.trim(), currentLineNumber);
                    if (validationResult.isNotValid()
                            && validationResultStrategy.shouldStopAtFirstError()) {
                        return;
                    }
                }
                line = lineParser.parseNext();
                currentLineNumber++;
            }
        } catch (IOException e) {
            validationResult.addError(ErrorMessage.of(e.getMessage()));
        }
    }

    private boolean checkIsWrappedWithinAngleBracketed(String line, long currentLineNumber) {
        int equalIndex = line.indexOf("=<");
        if (equalIndex < 0) {
            String errMsg = "A META header must be a key-value pair at line " + currentLineNumber;
            validationResult.addError(errMsg);
            return false;
        }
        int valueIndex = equalIndex + 1;
        if (line.charAt(valueIndex) != '<' || line.charAt(line.length() - 1) != '>') {
            String errMsg = "A META header value must be wrapped inside angle brackets (<...>)"
                    + " at line " +  currentLineNumber;
            validationResult.addError(errMsg);
            return false;
        }
        return true;
    }

    private boolean isAngleBracketWrappedString(String s) {
        if (s == null) {
            return false;
        }
        return s.startsWith("<") && s.endsWith(">");
    }

    private boolean isNotAngleBracketWrappedString(String s) {
        return !isAngleBracketWrappedString(s);
    }
}
