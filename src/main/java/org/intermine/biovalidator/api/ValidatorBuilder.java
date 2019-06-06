package org.intermine.biovalidator.api;

/*
 * Copyright (C) 2002-2019 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import org.intermine.biovalidator.api.strategy.ValidationResultStrategy;
import org.intermine.biovalidator.api.strategy.ValidatorStrictnessPolicy;
import org.intermine.biovalidator.validator.fasta.FastaDnaValidator;

import javax.annotation.Nonnull;
import java.io.File;

/**
 * Builder class to instantiate a Validator with customization
* @author deepak
 */
public final class ValidatorBuilder
{
    private Validator validator;
    private ValidationResultStrategy validationResultStrategy;
    private ValidatorStrictnessPolicy strictnessPolicy;

    private ValidatorBuilder() {
        this.validationResultStrategy = new DefaultValidationResultStrategy();
        this.strictnessPolicy = new DefaultValidatorStrictnessPolicy();
    }
    /**
     * Builds a validator implementation
     * @return an implementation of builder
     */
    public Validator build() {
        this.validator.applyValidationResultStrategy(validationResultStrategy);
        this.validator.applyValidatorStrictnessPolicy(strictnessPolicy);
        return validator;
    }

    /**
     * contruct a ValidatorBuilder with validator instance
     * @param validator instance of validator
     * @return ValidatorBuilder
     */
    public static ValidatorBuilder ofType(@Nonnull Validator validator) {
        ValidatorBuilder builder = new ValidatorBuilder();
        builder.validator = validator;
        return builder;
    }

    /**
     * A factory method to construct validator with a file based on the type argument
     * @param filename absolute file path
     * @param type used to identify validator instance
     * @return ValidatorBuilder
     * @throws IllegalArgumentException if type not found
     */
    public static ValidatorBuilder withFile(@Nonnull File filename, @Nonnull Type type)
                                                throws IllegalArgumentException {
        switch (type) {
            case FASTA_DNA:
                return ofType(new FastaDnaValidator(filename));
            default:
                throw new IllegalArgumentException("invalid file type");
        }
    }

    /**
     * A factory method to construct validator with a file based on the type argument
     * @param file file to be validated
     * @param type used to identify validator instance
     * @return ValidatorBuilder
     * @throws IllegalArgumentException if type not found
     */
    public static ValidatorBuilder withFile(@Nonnull String file, @Nonnull Type type)
                                                throws IllegalArgumentException {
        return withFile(new File(file), type);
    }

    /**
     * set the validator to stop validating as soon as it identifies first error
     * @return ValidatorBuilder
     */
    public ValidatorBuilder stopAtFirstError() {
        this.validationResultStrategy.stopAtFirstError();
        return this;
    }

    /**
     * Enables warning messages to be create by the validator
     * @return ValidatorBui
     */
    public ValidatorBuilder enableWarnings() {
        this.validationResultStrategy.enableWarnings();
        return this;
    }

    /**
     * Disable warning messages
     * @return ValidatorBuilder
     */
    public ValidatorBuilder disableWarnings() {
        this.validationResultStrategy.disableWarnings();
        return this;
    }

    /**
     * Enables error messages to be create by the validator
     * @return ValidatorBuilder
     */
    public ValidatorBuilder enableErrors() {
        this.validationResultStrategy.enableWarnings();
        return this;
    }

    /**
     * Disable error messages
     * @return ValidatorBuilder
     */
    public ValidatorBuilder disableErrors() {
        this.validationResultStrategy.disableWarnings();
        return this;
    }

    public enum Type
    {
        /**
         * Represents file validator types supported by builder
         */
        FASTA_DNA, FASTA_RNA, FASTA_PROTEIN
    }
}