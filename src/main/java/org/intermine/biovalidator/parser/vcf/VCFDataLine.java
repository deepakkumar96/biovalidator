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

/**
 * Represents a VCF file data line
 * @author deepak
 */
public class VCFDataLine implements VCFLine
{
    private String chrom;
    private String pos;
    private String id;
    private String ref;
    private String alt;
    private String qual;
    private String filter;
    private String info;
    private String format;

    /**
     * Construct a VCFDataLine with all the required parameters without FORMAT column
     * @param chrom chromosome value
     * @param pos the reference position
     * @param id identifier:  Semi-colon separated list of unique identifiers where available
     * @param ref reference base(s
     * @param alt alternate base(s)
     * @param qual quality
     * @param filter filter status
     * @param info additional information
     */
    public VCFDataLine(String chrom, String pos, String id, String ref, String alt, String qual,
                       String filter, String info) {
        this(chrom, pos, id, ref, alt, qual, filter, info, StringUtils.EMPTY);
    }

    /**
     * Construct a VCFDataLine with all the required parameters and FORMAT
     * @param chrom chromosome value
     * @param pos the reference position
     * @param id identifier:  Semi-colon separated list of unique identifiers where available
     * @param ref reference base(s
     * @param alt alternate base(s)
     * @param qual quality
     * @param filter filter status
     * @param info additional information
     * @param format vcf format
     */
    public VCFDataLine(String chrom, String pos, String id, String ref, String alt, String qual,
                       String filter, String info, String format) {
        this.chrom = chrom;
        this.pos = pos;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.qual = qual;
        this.filter = filter;
        this.info = info;
        this.format = format;
    }

    @Override
    public String toString() {
        return "VCFDataLine{"
                + "chrom='" + chrom + '\''
                + ", pos='" + pos + '\''
                + ", id='" + id + '\''
                + ", ref='" + ref + '\''
                + ", alt='" + alt + '\''
                + ", qual='" + qual + '\''
                + ", filter='" + filter + '\''
                + ", info='" + info + '\''
                + '}';
    }

    /**
     * Gets chrom
     *
     * @return value of chrom
     */
    public String getChrom() {
        return chrom;
    }

    /**
     * Gets pos
     *
     * @return value of pos
     */
    public String getPos() {
        return pos;
    }

    /**
     * Gets id
     *
     * @return value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets ref
     *
     * @return value of ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * Gets alt
     *
     * @return value of alt
     */
    public String getAlt() {
        return alt;
    }

    /**
     * Gets qual
     *
     * @return value of qual
     */
    public String getQual() {
        return qual;
    }

    /**
     * Gets filter
     *
     * @return value of filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * Gets info
     *
     * @return value of info
     */
    public String getInfo() {
        return info;
    }

    /**
     * Gets format
     *
     * @return value of format
     */
    public String getFormat() {
        return format;
    }
}
