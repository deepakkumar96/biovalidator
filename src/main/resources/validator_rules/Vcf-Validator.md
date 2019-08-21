## Validates VCF files

Validator for validating VCF files.

## Validation Rules:
* First Line must be a VCF version line
* Each VCF header should a key-value pair.
* Each VCF header value must be wrapped inside open and closing angle brackets eg. #INFO=<...>
* Each VCF header must be correctly formatted and required parameters should be present as specified in the VCF specification
* There must be a #CHROM header line between VCF headers and VCF data-lines.  
* CHROM must be present and must not contain any whitespace
* POS must be a numeric string must be sorted in increasing order.
* REF base is required and must be one of "A,C,G,T,N" (case insensitive), and cannot contains whitespace(s)
* ALT can be either an angle-bracket string or can only contain one of "A,C,G,T,N,*" and cannot contain any whitespace
* QUAL can either be missing value '.' or a numeric string
* FILTER and INFO must not contain whitespace 
