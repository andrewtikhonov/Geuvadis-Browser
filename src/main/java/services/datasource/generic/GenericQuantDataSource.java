package services.datasource.generic;

import services.data.DataSegment;
import services.data.DataFeature;
import services.data.FeatureType;

import java.io.InputStream;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.util.ProgressTracker;
import services.util.Util;


/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 08/11/2012
 * Time: 13:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class GenericQuantDataSource extends GenericDataSource {

    private final static Logger log = LoggerFactory.getLogger(GenericQuantDataSource.class);

    public String getStats() {
        StringBuilder b = new StringBuilder();

        b.append("features_total=");
        b.append(loaded_total);
        b.append(" ");

        for(String seg : segments0.keySet()) {
            int i = segments0.get(seg).getFeaturesList().size();
            b.append("'" + seg + "'=" + i + " ");
        }

        return b.toString();
    }

    public GenericQuantDataSource(InputStream stream) throws Exception {

        segments0 = initDataSource(stream, null);

        for (DataSegment s : segments0.values()) {
            s.sortFeatures();
        }
	}

    protected DataFeature getFeature(String featureType, String featureId,
                                     int start, int stop, double score, DataSegment segment) {

        loaded_total++;

        DataFeature newFeature = new DataFeature(featureType, featureId,
                segment.getSegmentID(), start, stop, score, 0 , 0); // rho = 0, pvalue = 0 ?

        try {
            segment.addFeature2(newFeature);
        } catch (Exception ex) {
            log.error("Error!", ex);
        }

   		return newFeature;
   	}


    //
    // G E T T E R S
    //
    public Collection<DataSegment> getSegments() {
        return segments0.values();
    }

    public HashMap<String, DataSegment> getSegmentsMap() {
        return segments0;
    }

    //public ArrayList<DataFeature> locateFeatures(String segmentId, int start, int stop) {
    //    return getSegmentsMap().get(segmentId).locateFeatures(start, stop);
   	//}

}

