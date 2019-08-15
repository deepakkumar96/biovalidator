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
import java.util.List;

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

    private List<String> samples;

    /**
     * Construct a VCFDataLine with all the required parametersw
     * @param chrom chromosome value
     * @param pos the reference position
     * @param id identifier:  Semi-colon separated list of unique identifiers where available
     * @param ref reference base(s
     * @param alt lternate base(s)
     * @param qual quality
     * @param filter filter status
     * @param info additional information
     * @param samples additional samples
     */
    public VCFDataLine(String chrom, String pos, String id, String ref, String alt, String qual,
                       String filter, String info, List<String> samples) {
        this.chrom = chrom;
        this.pos = pos;
        this.id = id;
        this.ref = ref;
        this.alt = alt;
        this.qual = qual;
        this.filter = filter;
        this.info = info;
        this.samples = samples;
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
                + ", samples=" + samples
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
     * Gets samples
     *
     * @return value of samples
     */
    public List<String> getSamples() {
        return samples;
    }
}
