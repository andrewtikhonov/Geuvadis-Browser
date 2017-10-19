package services.data;

import services.util.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 08/11/2012
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class DataSegment {

    public String segmentID;
    public String displayName;
    public String dataType;
    public int start;
    public int stop;

    public ArrayList<DataFeature> sortedFeatures = new ArrayList<DataFeature>();
    public Map<String, DataFeature> featureMap = null;

    public static FeatureStartComparator startComparator = new FeatureStartComparator();
    public static FeatureStopComparator stopComparator = new FeatureStopComparator();

    public DataSegment(String dataType, String segmentID, String displayName, int start, int stop) {
        this.dataType  = dataType;
        this.segmentID = segmentID;
        this.displayName = displayName;
        this.start = start;
        this.stop = stop;
    }

    public String getSegmentID() {
        return segmentID;
    }


    public ArrayList<DataFeature> getFeaturesList() {
        return sortedFeatures;
    }

    //   A D D   T H E N   S O R T
    //
    public void addFeature2(DataFeature feature) throws Exception {
        sortedFeatures.add(feature);
    }

    public void sortFeatures() throws Exception {
        Collections.sort(sortedFeatures, startComparator);
    }


    //   S O R T   W H E N   A D D I N G
    //
    //
    public int binaryLocateIndex(DataFeature feature) throws Exception {
        return Collections.binarySearch(sortedFeatures, feature, startComparator);
    }

    public void insertAtLocatedIndex(int index, DataFeature feature) throws Exception {

        if (index < 0) {

            sortedFeatures.add(-(index)-1, feature);

        } else {
            int size = sortedFeatures.size();

            // locate the end
            index++;

            for(;index < size;index++) {
                if (sortedFeatures.get( index ).getStart() > feature.getStart()) {
                    break;
                }
            }

            sortedFeatures.add(index, feature);
        }
    }


    //  L O C A T I N G
    //
    public ArrayList<DataFeature> locateFeatures(int start0, int stop0) {

        ArrayList<DataFeature> result = new ArrayList<DataFeature>();

        /*
        // locate the beginning
        int startIndex = Math.max(BinarySearch.locateBinaryIndex(sortedFeatures, start0, new EndGetter()) - 5, 0);
        int stopIndex = Math.min(BinarySearch.locateBinaryIndex(sortedFeatures, stop0, new StartGetter()) + 5,
                                sortedFeatures.size() - 1);
        */

        DataFeature stopFeature = new DataFeature(FeatureType.EXON, "", "", 0, start0, 0);
        DataFeature startFeature = new DataFeature(FeatureType.EXON, "", "", stop0, 0, 0);

        // locate the beginning
        int startIndex = Math.max(
                Math.abs( Collections.binarySearch(sortedFeatures, stopFeature, stopComparator) ) - 5,
                0);

        int stopIndex = Math.min(
                Math.abs( Collections.binarySearch(sortedFeatures, startFeature, startComparator) ) + 5,
                sortedFeatures.size() - 1);

        for(int index = startIndex; index <= stopIndex; index++) {
            DataFeature f = sortedFeatures.get(index);

            if (Util.isWithin(f.getStart(), start0, stop0) || Util.isWithin(f.getEnd(), start0, stop0)) {
                result.add(f);
            }
        }

        return result;
    }

    public DataFeature locateOneFeature(int start, int stop) {

        // locate
        int index = Collections.binarySearch(sortedFeatures,
                new DataFeature(FeatureType.EXON, "", "", start, stop, 0), startComparator);

        if (index < 0) {
            return null;
        }

        return sortedFeatures.get(index);
    }

    //
    //   F E A T U R E   M A P
    //
    // used for fast search by feature id
    public void createFeatureMap() {
        featureMap = new HashMap<String, DataFeature>();

        for(DataFeature df : sortedFeatures) {
            featureMap.put(df.getId(), df);
        }
    }


    public ArrayList<DataFeature> locateFeatureByID(String id) {
        ArrayList<DataFeature> result = new ArrayList<DataFeature>();

        if (featureMap != null) {
            DataFeature df = featureMap.get(id);

            if (df != null) {
                result.add(df);
            }

        }
        /*
        else {
            for(DataFeature df : sortedFeatures) {
                if (df.getId().equals(id)) {
                    result.add(df);

                    break;
                }
            }
        }
        */

        return result;
    }


    //   G E T T E R S
    //
    //
    public int getStart() {
        return start;
    }

    public int getStop() {
        return stop;
    }

    public String getDataType() {
        return dataType;
    }

    //
    //   A T T I C
    //

    /*
    public int locateFeatureIndex(int coordinate, int mode) {
        //log.info("--- locateFeatureIndex --- coordinate=" + coordinate + " mode=" + mode);

        int size = sortedFeatures.size();

        int delta = size / 2;
        int index = delta;

        for (;delta > 1;) {
            int fcoord = 0;

            switch (mode) {
                case START_COORD: fcoord = sortedFeatures.get(index).getStart(); break;
                case STOP_COORD: fcoord = sortedFeatures.get(index).getEnd(); break;
            }

            //log.info("index = " + index +" delta = " + delta + " fcoord = " + fcoord);

            if (coordinate < fcoord) {
                delta = delta / 2;
                index -= delta;
                continue;
            }

            if (coordinate >= fcoord) {
                delta = delta / 2;
                index += delta;
                continue;
            }
        }

        DataFeature f = sortedFeatures.get(index);

        log.info("located mode=" + mode + " coordinate=" + coordinate + " index="+ index);
        log.info("located feature=" + f.getFeaureID() + " start=" + f.getStart()+ " end=" + f.getEnd());

        return index;
    }
    */

    /*
    public HashMap<String, DataFeature> featureMap = new HashMap<String, DataFeature>();
    */

    /*
    public HashMap<String, DataFeature> getFeaturesMap() {
        return featureMap;
    }
    */

    /*
    public DataFeature getFeature(String featureId) {
        if (featureMap.containsKey(featureId)) {
            return featureMap.get( featureId );
        } else {
            return null;
        }
    }
    */

    /*
    public DataFeature getFeatureUnsafe(String featureId) {
        return featureMap.get( featureId );
    }
    */

    /*
    public boolean containsFeature(String featureId) {
        return featureMap.containsKey(featureId);
    }
    */

    /*
    public int getCoordAt(int index) {
        if (index < 0) {
            return 0;
        }
        if (index >= sortedFeatures.size()) {
            return 999999999;
        }
        return sortedFeatures.get( index ).getStart();
    }

    public int locateIndexToInsertAt(int coordinate) {
        int size = sortedFeatures.size();
        int delta = (size + 1) / 2;
        int index = delta;

        //log.info("locateIndexToInsertAt coordinate=" + coordinate + " size="+sortedFeatures.size()+ " start index="+index+" delta="+delta);

        for(;;) {

            int pos0 = getCoordAt( index - 1 );
            int pos1 = getCoordAt( index );

            if (coordinate == pos1) {

                //log.info("located same coordinate records at " + index);

                // locate the end
                index++;

                for(;index < size;index++) {
                    if (sortedFeatures.get( index ).getStart() > coordinate) {
                        break;
                    }
                }

                //log.info("located same coordinate records end at " + index);

                return index;
            }

            if (coordinate < pos1 && coordinate > pos0) {

                //log.info("located position at " + index);

                break;

            } else {

                if (coordinate < pos1) {
                    delta =  Math.max(1, (delta + 1) / 2);
                    index -= delta;

                    if (index == 0) {
                        //log.info("located position at the beginning of array");
                        break;
                    }

                    //log.info(coordinate + " is less than " + pos1 + " at " + (index + delta) + " jumping " + delta + " back");
                }

                if (coordinate > pos1) {
                    delta = Math.max(1, (delta + 1) / 2);
                    index += delta;

                    //log.info(coordinate + " is greater than " + pos1 + " at " + (index - delta) + " jumping " + delta + " forward");

                    if (index == size) {
                        //log.info("located position at the end of array");
                        break;
                    }
                }
            }
        }

        return index;
    }
    */

    /*
    public void addFeature(DataFeature feature) throws Exception {

        int index = 0;

        if (sortedFeatures.size() > 0) {
            index = locateIndexToInsertAt(feature.getStart());
        }

        sortedFeatures.add(index, feature);

    }
    */

}
