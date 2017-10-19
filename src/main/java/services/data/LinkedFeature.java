package services.data;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 04/12/2012
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
public class LinkedFeature {
    private double linkScore;
    private DataFeature feature;
    private boolean bestscore = false;

    public LinkedFeature(double linkScore, DataFeature feature){
        this.linkScore = linkScore;
        this.feature   = feature;
    }

    public double getLinkScore() {
        return linkScore;
    }

    public DataFeature getFeature() {
        return feature;
    }

    public boolean isBest() {
        return bestscore;
    }

    public void setBest(boolean bestscore) {
        this.bestscore = bestscore;
    }


}
