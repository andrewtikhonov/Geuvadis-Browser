package services.datasource.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.data.DataSegment;
import services.data.FeatureType;
import services.util.ProgressTracker;
import services.util.Util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 10/01/2013
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
public abstract class GenericDataSource implements DataSourceInterface {

    private final static Logger log = LoggerFactory.getLogger(GenericDataSource.class);

    protected HashMap<String, DataSegment> segments0;
    protected int loaded_total = 0;

    //
    // P R O C E S S I N G
    //
    public HashMap<String, DataSegment> initDataSource(InputStream inputstream,
                                                       DataSourceInterface linkedSource) throws Exception {

        Scanner scanner = new Scanner(inputstream);
        HashMap<String, DataSegment> segmap = new HashMap<String, DataSegment>();

        log.info("size = " + Util.humanizeSize(inputstream.available()));
        ProgressTracker tracker = new ProgressTracker(inputstream.available());

        try {
   			//first use a Scanner to get each line
            while ( scanner.hasNextLine() ) {
                String line = scanner.nextLine();

                if (line.length() > 0) {
                    processLine(line, segmap, linkedSource);
                }

                if (tracker.track(line.length())) {
                    log.info(tracker.report());
                }
   			}
   		} finally {
   			//ensure the underlying stream is always closed
   			scanner.close();
            System.gc();
   		}

        return segmap;
   	}

    abstract protected void processLine(String aLine, HashMap<String, DataSegment> segmentmap, DataSourceInterface linkedSource) throws Exception;

    protected DataSegment getSegment(String segmentId, HashMap<String, DataSegment> map) {
        if (map.containsKey(segmentId)) {
            return map.get(segmentId);
        }
        DataSegment newSegment = new DataSegment(FeatureType.CHROMOSOME, segmentId, segmentId, 1, 1);
        map.put(segmentId, newSegment);
   		return newSegment;
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

    public DataSegment locateSegment(String segmentId) {
        return getSegment(segmentId, getSegmentsMap());
   	}

    public ArrayList<DataFeature> locateFeatures(String segmentId, int start, int stop) {
        return locateSegment(segmentId).locateFeatures(start, stop);
   	}

    public ArrayList<DataFeature> locateFeatureByID(String id) {
        ArrayList<DataFeature> fl = new ArrayList<DataFeature>();

        for(DataSegment s : getSegments()) {
            //log.info("searching for " + id + " in segment " + s.getSegmentID());

            fl = s.locateFeatureByID(id);

            if (fl.size() > 0) {
                break;
            }
        }
        return fl;
   	}

    public ArrayList<DataFeature> locateFeatureByID(String featureId, String segmentId) {
        ArrayList<DataFeature> fl = new ArrayList<DataFeature>();

        DataSegment seg = getSegmentsMap().get(segmentId);

        if (seg != null) {
            //log.info("searching for " + featureId + " in segment " + segmentId);
            fl = seg.locateFeatureByID(featureId);
        }
        return fl;
   	}

}
