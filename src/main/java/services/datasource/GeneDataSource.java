package services.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.GeneFeature;
import services.util.BinaryCoordGetter;
import services.util.BinarySearch;
import services.util.Util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 28/11/2012
 * Time: 11:15
 * To change this template use File | Settings | File Templates.
 */
public class GeneDataSource {

    private final static Logger log = LoggerFactory.getLogger(GeneDataSource.class);

    private HashMap<String, List<GeneFeature>> chrGenes = null;
    private HashMap<String, GeneFeature> geneMap = null;

    public GeneDataSource(InputStream stream) throws Exception {

        geneMap = new HashMap<String, GeneFeature>();

        chrGenes = GeneDataLoader.loadData(stream);

        for (List l : chrGenes.values()) {
            // sort the stuff
            Collections.sort(l, new GeneFeatureStartComparator());
        }

        for (List<GeneFeature> l : chrGenes.values()) {
            for (GeneFeature g : l) {
                geneMap.put(g.getDisplayName(), g);
                geneMap.put(g.getStableID(), g);
            }
        }
    }

    public GeneFeature locateGene(String geneName) {
        return geneMap.get(geneName);
    }


    public static class GeneFeatureStartComparator implements Comparator<GeneFeature> {
        public int compare(GeneFeature g1, GeneFeature g2) {

            int s1 = 0;
            int s2 = 0;

            try {
                s1 = g1.getStart();
                s2 = g2.getStart();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (s1 > s2)
                return 1;
            else if (s1 < s2)
                return -1;
            else
                return 0;
        }
    }

    public static class GeneFeatureEndComparator implements Comparator<GeneFeature> {
        public int compare(GeneFeature g1, GeneFeature g2) {

            int e1 = 0;
            int e2 = 0;

            try {
                e1 = g1.getEnd();
                e2 = g2.getEnd();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (e1 > e2)
                return 1;
            else if (e1 < e2)
                return -1;
            else
                return 0;
        }
    }

    private class StartGetter implements BinaryCoordGetter {
        private List<GeneFeature> list;
        public StartGetter(List<GeneFeature> listToProcess){
            this.list = listToProcess;
        }
        public int getCoord(int index) {
            int coord = 0;
            try {
                coord = list.get(index).getStart();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return coord;
        }
    }

    private class EndGetter implements BinaryCoordGetter {
        private List<GeneFeature> list;
        public EndGetter(List<GeneFeature> listToProcess){
            this.list = listToProcess;
        }
        public int getCoord(int index) {
            int coord = 0;
            try {
                coord = list.get(index).getEnd();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return coord;
        }
    }

    public ArrayList<GeneFeature> locateFeatures(String chr, int start0, int stop0) {

        List<GeneFeature> listToProcess = chrGenes.get(chr);

        ArrayList<GeneFeature> result = new ArrayList<GeneFeature>();

        // locate the beginning
        int startIndex = Math.max(BinarySearch.locateBinaryIndex(listToProcess, start0, new EndGetter(listToProcess)) - 5, 0);
        int stopIndex = Math.min(BinarySearch.locateBinaryIndex(listToProcess, stop0, new StartGetter(listToProcess)) + 5,
                listToProcess.size() - 1);

        /*

        DataFeature stopFeature = new DataFeature(FeatureType.EXON, "1", 0, start0, 0);
        DataFeature startFeature = new DataFeature(FeatureType.EXON, "1", stop0, 0, 0);

        // locate the beginning
        int startIndex = Math.max(locateFeatureIndex2(stopFeature, STOP_COORD) - 5, 0);
        int stopIndex = Math.min(locateFeatureIndex2(startFeature, START_COORD) + 5,
                                    sortedFeatures.size() - 1);
        */

        for(int index = startIndex; index <= stopIndex; index++) {
            GeneFeature g = listToProcess.get(index);

            try {
                if (Util.isWithin(g.getStart(), start0, stop0) || Util.isWithin(g.getEnd(), start0, stop0)) {
                    result.add(g);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    public static void main(String[] args) {

        try {
            GeneDataSource source = new GeneDataSource(
                    new FileInputStream(System.getProperty("gene.fname")));

            String chr = "1";
            int start = 696291;
            int stop = 787400;

            log.info("selecting " + chr + ":" + start + "-" + stop);

            ArrayList<GeneFeature> result = null;

            long startTime = System.currentTimeMillis();

            for(int i=9;i<1000;i++) {
                result = source.locateFeatures(chr, start, stop);
            }

            long estimatedTime = System.currentTimeMillis() - startTime;

            log.info("Estimated run time:" + estimatedTime + " Millis");

            log.info("result.size() = " + result.size());
            try {
                for (GeneFeature g : result) {
                    log.info("1 g = " + g.getStableID() + " " + g.getDisplayName() + " start:" + g.getStart() +
                            " end:" + g.getEnd());
                }

            } catch (Exception ex) {
                log.error("Error!", ex);
            }
        } catch (Exception ex) {
            log.error("Error!", ex);
        }


    }

}
