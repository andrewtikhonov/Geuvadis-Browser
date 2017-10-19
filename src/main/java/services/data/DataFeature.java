package services.data;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 08/11/2012
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class DataFeature {

    private String id;
    private int start;
    private int end;
    private String chr;
    private String type;
    private double score;
    private double rho;
    private double pvalue;

    private ArrayList<LinkedFeature> linked;

    public DataFeature(String dataType, String featureId, String chrId, int start, int end, double score, double rho, double pvalue) {
        this.type = dataType;
        this.id = featureId;
        this.chr = chrId;
        this.start = start;
        this.end = end;
        this.score = score;
        this.rho = rho;
        this.pvalue = pvalue;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public double getScore() { return score; }

    public double getRho() { return rho; }

    public double getPvalue() { return pvalue; }

    public String getChr() {
        return chr;
    }

    public ArrayList<LinkedFeature> getLinked() {
        if (linked == null) {
            linked = new ArrayList<LinkedFeature>();
        }
        return linked;
    }

    public void setScore(double score) {
        this.score = score;
    }

}

