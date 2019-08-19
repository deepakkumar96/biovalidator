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
import java.util.Optional;

/**
 * VCF Data parser
 * @author deepak kumar
 */
public class VCFDataParser implements Parser<Optional<VCFLine>>
{
    /**
     * Represents VCF HEADER LINE
     */
    public static final String VCF_HEADER_LINE = "#CHROM";
    private static final int LEAST_COLUMN_COUNT = 8;
    private Parser<String> lineByLineParser;
    private long headersLinesCount;
    private int headersColumnCount;
    private long totalDataLineRead;

    /**
     * Construct a VCFDataParser with input stream
     * @param inputStreamReader inputStream to file
     * @throws ParsingException if parsing fails
     */
    public VCFDataParser(InputStreamReader inputStreamReader) throws ParsingException {
        lineByLineParser = new GenericLineByLineParser(inputStreamReader);
        headersLinesCount = 1;
        totalDataLineRead = 0;
        readAllHeaders();
    }

    /**
     * Exhaust all meta data from VCF, so that parseNext() can return only data lines not headers.
     * @throws ParsingException if fails
     */
    private void readAllHeaders() throws ParsingException {
        String line = lineByLineParser.parseNext();
        while (line != null && line.startsWith("#") && !line.startsWith(VCF_HEADER_LINE)) {
            line = lineByLineParser.parseNext();
            headersLinesCount++;
        }
        //count length of columns
        if (line != null && line.startsWith(VCF_HEADER_LINE)) {
            String[] data = StringUtils.split(line, '\t');
            if (data != null) {
                headersColumnCount = data.length;
            }
        }
        // assign totalLines equals total headers line
        totalDataLineRead = headersLinesCount;
    }

    @Override
    public Optional<VCFLine> parseNext() throws ParsingException {
        totalDataLineRead++;
        Optional<String> currentLineOpt = Optional.ofNullable(lineByLineParser.parseNext());
        if (currentLineOpt.isPresent()) {
            return Optional.of(parseVcfLine(currentLineOpt.get()));
        }
        return Optional.empty();
    }

    private VCFLine parseVcfLine(String line) throws ParsingException {
        String[] data = StringUtils.split(line, '\t');

        if (data.length >= LEAST_COLUMN_COUNT && data.length == headersColumnCount) {
            return new VCFDataLine(data[0], data[1], data[2], data[3], data[4],
                    data[5], data[6], data[7]);
        } else {
            throw new ParsingException("wrong number of column at line " + totalDataLineRead);
        }
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
