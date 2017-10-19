package services.datasource;

import services.data.DataFeature;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 10/01/2013
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
public interface AggregatedSourceInterface {
    public ArrayList<DataFeature> locateQuantFeatures(String segmentId, int start, int stop);
    public ArrayList<DataFeature> locateEQTLFeatures(String segmentId, int start, int stop);
    public ArrayList<DataFeature> locateQuantFeatureByID(String featureId);
    public ArrayList<DataFeature> locateEQTLFeatureByID(String featureId);
    public ArrayList<DataFeature> locateQuantFeatureByID(String featureId, String segmentId);
    public ArrayList<DataFeature> locateEQTLFeatureByID(String featureId, String segmentId);

}
