package services.datasource.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.data.DataSegment;
import services.data.FeatureType;
import services.data.LinkedFeature;

import java.io.InputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 05/12/2012
 * Time: 10:32
 * To change this template use File | Settings | File Templates.
 */
public abstract class GenericEQTLDataSource extends GenericDataSource {

    private final static Logger log = LoggerFactory.getLogger(GenericEQTLDataSource.class);

    public String getStats() {
        return "features_total="+getFeaturesTotal()+" features_linked="+getFeaturesLinked()+" features_not_linked="+getFeaturesNotLinked();
    }


    public int getFeaturesTotal() {
        return features_total;
    }

    public int getFeaturesLinked() {
        return features_linked;
    }

    public int getFeaturesNotLinked() {
        return features_not_linked;
    }

    private int features_total      = 0;
    private int features_linked     = 0;
    private int features_not_linked = 0;

    protected void incFeaturesTotal() {
        features_total++;
    }

    protected void incFeaturesLinked() {
        features_linked++;
    }

    protected void incFeaturesNotLinked() {
        features_not_linked++;
    }

	public GenericEQTLDataSource(InputStream stream) throws Exception {
        this(stream, null);
	}

    public GenericEQTLDataSource(InputStream stream, DataSourceInterface linkedSource) throws Exception {
        segments0 = initDataSource(stream, linkedSource);

        /*
        for (DataSegment s : segments0.values()) {

            log.info("sorting segment " + s.getSegmentID());

            s.sortFeatures();
        }
        */

        for (DataSegment s : segments0.values()) {

            log.info("processing segment " + s.getSegmentID());

            processFeatures(s);
        }

        /*
        for (DataSegment s : segments0.values()) {
            log.info("filtering segment " + s.getSegmentID());

            filterFeatures(s);
        }
        */

        for (DataSegment s : segments0.values()) {
            log.info("validating segment " + s.getSegmentID());

            validateFeatures(s);
        }

        log.info("creating feature map");

        for (DataSegment s : segments0.values()) {
            s.createFeatureMap();
        }

    }

    protected String getFeatureType(String featureId) {
        String type = FeatureType.SNIP;

        if (featureId.startsWith("snp")) {
            type = FeatureType.SNIP;
        } else if (featureId.startsWith("indel")) {
            type = FeatureType.INDEL;
        }

        return type;
    }

    protected DataFeature getFeature(String featureType, String featureId, int start, int end,
                                   double score, double rho, double pvalue, DataSegment segment) {

        loaded_total++;

        // create new feature
        DataFeature newFeature = new DataFeature(featureType,
                featureId, segment.getSegmentID(), start, end, score, rho, pvalue);

        try {
            int index = segment.binaryLocateIndex(newFeature);

            if (index >= 0) {

                DataFeature locatedFeature = segment.getFeaturesList().get(index);

                // make sure Ids match
                if (locatedFeature.getId().equals(featureId)) {
                    newFeature = locatedFeature;
                }
            } else {

                segment.insertAtLocatedIndex(index, newFeature);
            }
        } catch (Exception ex) {
            log.error("Error!", ex);
        }

   		return newFeature;
   	}


    /*
    public void filterFeatures(DataSegment segment) {
        ArrayList<DataFeature> list = segment.getFeaturesList();
        ArrayList<DataFeature> listToFemove = new ArrayList<DataFeature>();

        DataFeature f = null;

        //int remove_cnt = 0;
        int processed0 = 0;
        int processed1 = list.size() / 10;

        for(DataFeature f0 : list) {

            // percentage
            processed0++;

            if (processed0 > processed1) {
                log.info(((long)processed0 * 100 / list.size()) + "%");
                processed1 += list.size() / 10;
            }

            if (f != null &&
                    f0.getId().equals(f.getId())) {

                // set maximum score
                f.setScore( Math.max(f0.getScore(), f.getScore()) );

                // copy attributes
                f.getLinked().addAll(f0.getLinked());

                listToFemove.add(f0);

                //remove_cnt++;

                //log.info("merging " + f0.getId() + " attribute=" +
                //        f0.getLinked().get(0) + " into " + f.getId());

                continue;
            }

            f = f0;
        }

        log.info("removing " + listToFemove.size() + " duplicates");

        // remove all items
        list.removeAll(listToFemove);

        log.info("garbage collecting");
        System.gc();
    }
    */

    public void processFeatures(DataSegment segment) {
        HashMap<String, LinkedFeature> scoreMap = new HashMap<String, LinkedFeature>();
        ArrayList<DataFeature> list = segment.getFeaturesList();

        for(DataFeature f0 : list) {

            LinkedFeature linked0 = f0.getLinked().get(0);
            LinkedFeature linked1 = scoreMap.get(linked0.getFeature().getId());

            if (linked1 == null) {
                scoreMap.put(linked0.getFeature().getId(), linked0);
            } else {

                // select the max score
                if (linked0.getLinkScore() > linked1.getLinkScore()) {

                    // keep the new
                    scoreMap.put(linked0.getFeature().getId(), linked0);
                }
            }
        }

        log.info("located " + scoreMap.values().size() + " best links");

        for (LinkedFeature l0 : scoreMap.values()) {
            l0.setBest(true);
        }
    }

    public void validateFeatures(DataSegment segment) {
        ArrayList<DataFeature> list = segment.getFeaturesList();

        int feature_count = list.size();
        int link_count = 0;
        int best_total = 0;

        int[] best = new int[20];
        int muulti_best = 0;

        Arrays.fill(best, 0);

        for(DataFeature f0 : list) {

            int best_count = 0;

            link_count += f0.getLinked().size();

            for (LinkedFeature lf0 : f0.getLinked()) {

                if (lf0.isBest()) {

                    best_total++;
                    best_count++;
                }
            }

            if (best_count > 0) {
                if (best_count >= 20) {
                    //log.info(f0.getId() + " contains " + best_count + " best links");

                    muulti_best++;
                } else {
                    best[ best_count ]++;
                }
            }
        }

        for (int i=0;i<best.length;i++) {
            if (best[ i ] > 0) {
                log.info("      " + best[ i ] + " level " + i + " best links");
            }
        }

        if (muulti_best > 0) {
            log.info("      " + muulti_best + " level 'MORE THAN 20' best links");
        }

        log.info("features " + feature_count + " links " + link_count + " of them best " + best_total);
    }

}

