package org.intermine.biovalidator.validator.gff3;

/*
 * Copyright (C) 2002-2019 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.intermine.biovalidator.api.ErrorMessage;
import org.intermine.biovalidator.api.Parser;
import org.intermine.biovalidator.api.ValidationFailureException;
import org.intermine.biovalidator.api.ValidationResult;
import org.intermine.biovalidator.api.WarningMessage;
import org.intermine.biovalidator.parser.Gff3FeatureParser;
import org.intermine.biovalidator.validator.AbstractValidator;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author deepak
 */
public class Gff3Validator extends AbstractValidator
{
    private static final String SEQUENCE_ID_VALID_PATTERN = "[a-zA-Z0-9.:^*$@!+_?-|]+";
    private static final String VERSION_NUMBER_PATTERN = "(\\d+\\.)?(\\d+\\.)?(\\*|\\d+)";
    private static final String FLOATING_POINT_NUM_PATTERN = "[0-9]*\\.?[0-9]+";

    private static final String SEQUENCE_REGION_DIRECTIVE = "##sequence-region";
    private static final String FASTA_DIRECTIVE = "##FASTA";
    private static final String GFF3_HEADER = "##gff-version";

    private InputStreamReader inputStreamReader;

    private Set<String> uniqueIdAttributesSet;
    private Set<String> uniqueNameAttributeSet;

    private Map<String, SequenceRegion> sequenceRegionDirectives;

    /**
     * Contruct a Gff3Validator with an input stream
     * @param inputStreamReader input source
     */
    public Gff3Validator(InputStreamReader inputStreamReader) {
        this.inputStreamReader = inputStreamReader;
        this.uniqueIdAttributesSet = new HashSet<>();
        this.uniqueNameAttributeSet = new HashSet<>();

        //store start/end coordinate all the ##sequence-region directives available in the file
        this.sequenceRegionDirectives = new HashMap<>();
    }

    @Nonnull
    @Override
    public ValidationResult validate() throws ValidationFailureException {
        try (Parser<Optional<Gff3Line>> parser = new Gff3FeatureParser(inputStreamReader)) {
            long currentLineNum = 1;
            Optional<Gff3Line> lineOpt = parser.parseNext();

            if (lineOpt.isPresent() && !isValidGff3HeaderLine(lineOpt.get())) {
                validationResult.addError(ErrorMessage.of("Invalid Gff file"));
            }

            while (lineOpt.isPresent()) {
                Gff3Line line = lineOpt.get();

                if (line instanceof Gff3CommentLine) { //if line a comment line
                    Gff3CommentLine comment = (Gff3CommentLine) line;
                    if (comment.getComment().startsWith(FASTA_DIRECTIVE)) { //End of GFF3 content
                        return validationResult;
                    }
                    processCommentLine(comment);
                } else {
                    FeatureLine feature = (FeatureLine) line;
                    validateFeature(feature, currentLineNum);
                }

                if (!validationResult.isValid()) {
                    return validationResult;
                }
                lineOpt = parser.parseNext(); //next featureLine
                currentLineNum++;
            }
            return validationResult;
        } catch (IOException e) {
            throw new ValidationFailureException(e.getMessage());
        }
    }

    private void validateFeature(FeatureLine feature, long currentLineNum) {
        String seqId = feature.getSeqId();

        if (!seqId.matches(SEQUENCE_ID_VALID_PATTERN)) {
            addError("Invalid Sequence Id at " + currentLineNum);
        }

        validateStartEndCoordinates(feature, currentLineNum);

        String score = feature.getScore();
        if (!".".equals(score) && !score.matches(FLOATING_POINT_NUM_PATTERN)) {
            addError("score value must be floating point number at line " + currentLineNum);
        }

        String strand = feature.getStrand();
        if (!StringUtils.equalsAny(strand, ".", "-", "+")) { //checks strand value is valid
            addError("strand value must be one of ('-', '+') at line " + currentLineNum);
        }

        String phase = feature.getPhase();
        if (!".".equals(phase) && !isInteger(phase)) {
            addError("phase value can only be one of 0, 1, 2 or '.' at line " + currentLineNum);
        }
        if ("CDS".equals(phase) && !StringUtils.equalsAny(phase, "0", "1", "2")) {
            addError("phase is required for CDS and can only be 0, 1 or 2");
        }


        // Validating Attribute column
        if (!isAttributesEncoded(feature.getAttributes())) {
            addError("attribute is not encoded");
        }

        validateFeatureAttributes(feature, currentLineNum);

        //check unique ID attributes
        Map<String, String> keyValPairAttributes = feature.getAttributesMapping();
        if (keyValPairAttributes.containsKey("ID")) {
            uniqueIdAttributesSet.add(keyValPairAttributes.get("ID"));
        }
        if (keyValPairAttributes.containsKey("Name")) {
            uniqueNameAttributeSet.add(keyValPairAttributes.get("Name"));
        }
    }

    private void validateFeatureAttributes(FeatureLine feature, long currentLineNum) {
        validateFeatureAttributesKeyAndValue(feature);
        if (!validationResult.isValid()) {
            return;
        }

        //If feature has a parent key then check its parent exist or not
        Map<String, String> keyValPairAttributes = feature.getAttributesMapping();
        if (keyValPairAttributes.containsKey("Parent")) {
            String parentVal = keyValPairAttributes.get("Parent");
            if (!uniqueIdAttributesSet.contains(parentVal)
                    && !uniqueNameAttributeSet.contains(parentVal)) {
                addError("Parent '" + parentVal + "' not found at line " + currentLineNum);
            }
        }
    }

    /**
     * Test whether a ##gff-version header is correct
     * Rules:
     *      1. must start with ##gff-version
     *      2. version must start with 3
     *      3. version itself must be a valid version number
     * @param line comment line
     * @return whether gff3 header is valid or not
     */
    private boolean isValidGff3HeaderLine(Gff3Line line) {
        if (line instanceof Gff3CommentLine) {
            Gff3CommentLine comment = (Gff3CommentLine) line;
            String headerLine = comment.getComment();
            if (!headerLine.startsWith(GFF3_HEADER)) {
                return false;
            }

            /*
                check gff3 version 3 is valid or not
                GFF3 version format: ##gff-version 3.#.#
             */
            String[] values = headerLine.split("\\s+");
            if (values.length < 2) {
                return false;
            }
            String version = values[1];
            if (!version.startsWith("3") && !version.matches(VERSION_NUMBER_PATTERN)) {
                return false;
            }
        }
        return false;
    }

    /**
     * validates key and value of the attribute column of a feature
     * @param feature feature to be validated
     */
    private void validateFeatureAttributesKeyAndValue(FeatureLine feature) {
        Map<String, String> attributesMapping = feature.getAttributesMapping();
        Set<String> uniqueKeys = new HashSet<>();
        for (Map.Entry<String, String> entry: attributesMapping.entrySet()) {
            String attrTag = entry.getKey();
            String attrVal = entry.getValue();

            if (StringUtils.isBlank(attrTag)) {
                addWarning("attribute key is missing or empty");
            }
            if (StringUtils.isBlank(attrVal)) {
                addWarning("attribute value is missing or empty");
            }
            if (uniqueKeys.contains(attrTag)) {
                addError("Tag '" + attrTag + "' is duplicated");
            } else {
                uniqueKeys.add(attrTag);
            }
            if (!validationResult.isValid()) {
                return;
            }
        }
    }

    /**
     * Test whether given attribute column's value is encoded|escaped or not
     * @param attributes attribute string
     * @return true or false
     */
    private boolean isAttributesEncoded(String attributes) {
        return true; // TODO
    }

    /**
        Validates start and end coordinate column of a feature
        @param feature feature to be validated
     */
    private void validateStartEndCoordinates(FeatureLine feature, long currentLineNum) {
        if (!NumberUtils.isParsable(feature.getStartCord())) {
            String invalidStartCordMsg = "start coordinate value is not a number";
            validationResult.addError(ErrorMessage.of(invalidStartCordMsg));
            if (validationResultStrategy.shouldStopAtFirstError()) {
                return;
            }
        }
        if (!NumberUtils.isParsable(feature.getEndCord())) {
            String invalidStartCordMsg = "end coordinate value is not a number";
            validationResult.addError(ErrorMessage.of(invalidStartCordMsg));
            if (validationResultStrategy.shouldStopAtFirstError()) {
                return;
            }
        }
        long startCord = Long.parseLong(feature.getStartCord());
        long endCord = Long.parseLong(feature.getEndCord());

        //start and end can't be zero and end should be greater than start
        if (startCord < 1 || endCord < 1 || endCord < startCord) {
            String coordinateErrMsg = "Start must be greater than zero and"
                    + " less or equal to end coordinate";
            validationResult.addError(ErrorMessage.of(coordinateErrMsg));
            if (validationResultStrategy.shouldStopAtFirstError()) {
                return;
            }
        }

        /*
        if ##sequence-region directive is defined for the current seqId then check
        start and end coordinate of current feature should be within the range of
        defined ##sequence-region directive
        */
        String seqId = feature.getSeqId();
        Map<String, String> featureAttributes = feature.getAttributesMapping();

        if (featureAttributes.containsKey("Is_circular")) {
            boolean isCircularValueTrue = Boolean.parseBoolean(featureAttributes.get("Is_circular"));
            if (isCircularValueTrue) {
                return; // if a feature is circular then ignore ##sequence-region range check
            }
        }
        if (sequenceRegionDirectives.containsKey(seqId)) {
            SequenceRegion seqRegion = sequenceRegionDirectives.get(seqId);
            if (startCord < seqRegion.getSequenceRegionStart()
                || endCord > seqRegion.getSequenceRegionEnd()) {
                String msg = "start/end coordinate of seqId " + seqId + " is not within the range"
                    + "of " + SEQUENCE_REGION_DIRECTIVE + " at line " + currentLineNum;
                addError(msg);
            }
        }
    }

    /**
     * checks whether string is number or not
     * @param phase value to be tested
     * @return true or false
     */
    private boolean isInteger(String phase) {
        return phase.matches("[0-9]{1,9}"); //TODO need to be refactored
    }

    private void processCommentLine(Gff3CommentLine commentLine) {
        String comment = commentLine.getComment();

        // Process ##sequence-region directive
        if (comment.startsWith(SEQUENCE_REGION_DIRECTIVE)) {
            ImmutablePair<String, SequenceRegion> seqIdAndSequenceRegionPair =
                parseSequenceRegion(comment);
            if (!seqIdAndSequenceRegionPair.equals(ImmutablePair.nullPair())) { //if parsed
                String seqId =  seqIdAndSequenceRegionPair.getLeft();
                if (sequenceRegionDirectives.containsKey(seqId)) {
                    String msg = "Found more than one entry for "
                        + SEQUENCE_REGION_DIRECTIVE + seqId;
                    addWarning(msg);
                } else {
                    sequenceRegionDirectives.put(seqId, seqIdAndSequenceRegionPair.getRight());
                }
            }
        }
    }

    /**
     * Parse ##sequence-region derective and returns a pair of seqId and SequenceRegion
     * Format of this directive is:
     *      ##sequence-region {@literal <seqId>} {@literal <start>} {@literal <end>}
     * @param comment comment to be parse
     * @return pair of seqId and SequenceRegion
     */
    private ImmutablePair<String, SequenceRegion> parseSequenceRegion(String comment) {
        if (StringUtils.isNoneBlank(comment)) {
            String[] values = comment.split("\\s+"); //split by skipping empty spaces
            if (values.length >= 4) {
                String seqId = values[1];
                String start = values[2];
                String end = values[3];
                if (NumberUtils.isParsable(start) && NumberUtils.isParsable(end)) {
                    long startValue = Long.parseLong(start);
                    long endValue = Long.parseLong(end);
                    return ImmutablePair.of(seqId, SequenceRegion.of(startValue, endValue));
                }
            }
        }
        return ImmutablePair.nullPair();
    }

    private void addError(String msg) {
        validationResult.addError(ErrorMessage.of(msg));
    }

    private void addWarning(String msg) {
        validationResult.addWarning(WarningMessage.of(msg));
    }
}