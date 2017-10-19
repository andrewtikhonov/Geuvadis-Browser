package services.datasource.generic;

import services.data.DataFeature;
import services.data.DataSegment;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 10/01/2013
 * Time: 14:58
 * To change this template use File | Settings | File Templates.
 */
public interface DataSourceInterface {
    public DataSegment                  locateSegment(String segmentId);
    public ArrayList<DataFeature>       locateFeatures(String segmentId, int start, int stop);
    public ArrayList<DataFeature>       locateFeatureByID(String featureId);
    public ArrayList<DataFeature>       locateFeatureByID(String featureId, String segmentId);
}
