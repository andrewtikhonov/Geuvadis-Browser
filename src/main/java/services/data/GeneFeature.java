package services.data;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 21/03/2013
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public class GeneFeature {

    private String displayName;
    private String stableID;
    private String biotype;
    private String chr;
    private int    start;
    private int    end;
    private String description;

    public GeneFeature(String displayName, String stableID, String biotype, String description, String chr, int start, int end){
        this.displayName = displayName;
        this.stableID    = stableID;
        this.biotype = biotype;
        this.chr         = chr;
        this.start       = start;
        this.end         = end;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getStableID() {
        return stableID;
    }

    public String getBiotype() {
        return biotype;
    }

    public String getChr() {
        return chr;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getDescription() {
        return description;
    }
}
