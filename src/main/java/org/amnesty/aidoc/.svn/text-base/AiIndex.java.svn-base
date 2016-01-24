package org.amnesty.aidoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class representing an AiIndex with routines parse a String into an AiIndex
 * and format an AiIndex into a String.
 * 
 * @author chatch
 */
public class AiIndex implements Comparable<Object> {
    public static final Pattern aiClassPattern = Pattern
            .compile("[A-Z]{3}[0-9]{2}");

    public static final Pattern aiIndexPattern = Pattern
            .compile("([A-Z]{3})[ ]?+([0-9]{2})/([0-9]{3})/([0-9]{4})");

    private String aiClass;

    private String docnum;

    private String year;

    public AiIndex(String aiClass, String docnum, String year) {
        setAiClass(aiClass);
        setDocnum(docnum);
        setYear(year);
    }

    public String getAiClass() {
        return aiClass;
    }

    public void setAiClass(String aiClass) {
        Matcher matcher = aiClassPattern.matcher(aiClass);
        boolean matchFound = matcher.find();
        if (matchFound == false) {
            throw new IllegalArgumentException("aiClass format [" + aiClass
                    + "] not valid");
        }
        this.aiClass = aiClass;
    }

    public String getDocnum() {
        return docnum;
    }

    public void setDocnum(String docnum) {
        this.docnum = docnum;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Parse a string representation of an aiIndex into its parts.
     * 
     * @param aiIndex
     *            index in format "AFR 19/001/2006" or "AFR19/001/2006"
     * @return AiIndex instance representing the passed aiIndex String
     */
    public static final AiIndex parse(String aiIndex) {
        
        Matcher matcher = aiIndexPattern.matcher(aiIndex);
        boolean matchFound = matcher.find();
        if (matchFound == false) {
            throw new IllegalArgumentException("aiIndex format [" + aiIndex
                    + "] not valid");
        }
        
        String aiClassStr = matcher.group(1) + matcher.group(2);
        String docnumStr = matcher.group(3);
        String yearStr = matcher.group(4);
        
        return new AiIndex(aiClassStr, docnumStr, yearStr);
    }

    /**
     * Render AiIndex in the standard format as described by aiIndexPattern
     */
    public String toString() {
        StringBuffer aiIndexStr = new StringBuffer(16);
        
        aiIndexStr.append(aiClass.substring(0, 3));
        aiIndexStr.append(" ");
        aiIndexStr.append(aiClass.substring(3, 5));
        aiIndexStr.append("/");
        aiIndexStr.append(docnum);
        aiIndexStr.append("/");
        aiIndexStr.append(year);
        
        return aiIndexStr.toString();
    }

    /**
     * Render in the standard format
     */
    public boolean equals(AiIndex index) {
        return this.aiClass.equals(index.getAiClass())
                && this.docnum.equals(index.getDocnum())
                && this.year.equals(index.getYear());
    }
    
    public int hashCode() {
        return toString().hashCode();
    }

    public int compareTo(Object o) {
        return this.toString().compareTo(o.toString());
    }

}
