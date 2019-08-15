package org.intermine.biovalidator.parser.vcf;

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
import org.intermine.biovalidator.api.Parser;
import org.intermine.biovalidator.api.ParsingException;
import org.intermine.biovalidator.parser.GenericLineByLineParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Optional;

/**
 * VCF Data parser
 * @author deepak kumar
 */
public class VCFDataParser implements Parser<Optional<VCFLine>>
{
    private static final String VCF_HEADER_LINE = "#CHROM";
    private Parser<String> lineByLineParser;
    private long headersLinesCount;

    /**
     * Construct a VCFDataParser with input stream
     * @param inputStreamReader inputStream to file
     * @throws ParsingException if parsing fails
     */
    public VCFDataParser(InputStreamReader inputStreamReader) throws ParsingException {
        lineByLineParser = new GenericLineByLineParser(inputStreamReader);
        headersLinesCount = 1;
        readAllHeaders();
    }

    /**
     * Exhaust all meta data from VCF
     * @throws ParsingException if fails
     */
    private void readAllHeaders() throws ParsingException {
        String line = lineByLineParser.parseNext();
        while (line != null && line.startsWith("#") && !line.startsWith(VCF_HEADER_LINE)) {
            line = lineByLineParser.parseNext();
            headersLinesCount++;
        }
    }

    @Override
    public Optional<VCFLine> parseNext() throws ParsingException {
        Optional<String> currentLineOpt = Optional.ofNullable(lineByLineParser.parseNext());
        if (currentLineOpt.isPresent()) {
            return parseVcfLine(currentLineOpt.get());
        }
        return Optional.empty();
    }

    private Optional<VCFLine> parseVcfLine(String line) {
        String[] data = StringUtils.split(line, '\t');
        if (data.length >= 8) {
            VCFDataLine dataLine = new VCFDataLine(data[0], data[1], data[2], data[3], data[4],
                    data[5], data[6], data[7], Collections.emptyList());
            return Optional.of(dataLine);
        }
        return Optional.empty();
    }

    @Override
    public void close() throws IOException {
        lineByLineParser.close();
    }

    /**
     * Gets headersLinesCount
     *
     * @return value of headersLinesCount
     */
    public long getHeadersLinesCount() {
        return headersLinesCount;
    }
}
