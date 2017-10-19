package services.datasource.quant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.data.DataSegment;
import services.data.FeatureType;
import services.datasource.generic.DataSourceInterface;
import services.datasource.generic.GenericQuantDataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 10/01/2013
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class TransQuantSource extends GenericQuantDataSource {

    private final static Logger log = LoggerFactory.getLogger(ExonQuantSource.class);

    public TransQuantSource(InputStream stream) throws Exception {
        super(stream);

        // for transcripts we need a MAP
        for (DataSegment s : segments0.values()) {
            s.createFeatureMap();
        }
   	}

    protected void processLine(String aLine, HashMap<String, DataSegment> segmentmap,
                               DataSourceInterface linkedSource) throws Exception {

   	    String[] parts = aLine.split("\t");

        //ENST00000000233.5_127228399_127231759	ENSG00000004059.5	7	127228399	4.829	43.707	103.341
        //ENST00000000412.3_9092961_9102551	ENSG00000003056.3	12	9092961	9.322	26.023	49.411
        //ENST00000000442.6_64073050_64084210	ENSG00000173153.9	11	64073050	0.000	4.079	10.930

   		if (parts.length < 7) {
            throw new Exception("Parsing Error: A line doesn't have the right number of fields ["+aLine+"]");
        }

        if ("TargetID".equals(parts[0])) {
            // first line, skip
            return;
        }

        DataSegment segment = getSegment(parts[2], segmentmap);

        String targetID   = parts[0];
        String[] parts2   = targetID.split("_");

        String exonID     = parts2[0];

        String[] parts3  = exonID.split("\\.");
        String transID    = parts3[0];

        try {
            int rangeStart = Integer.parseInt(parts2[1]);
            int rangeStop  = Integer.parseInt(parts2[2]);

            Double min    = Double.parseDouble(parts[4]);
            Double avg    = Double.parseDouble(parts[5]);
            Double max    = Double.parseDouble(parts[6]);

            //getFeature(targetID + "_MIN", rangeStart, rangeStop, min, segment);
            //getFeature(targetID + "_AVG", rangeStart, rangeStop, avg, segment);
            //getFeature(targetID + "_MAX", rangeStart, rangeStop, max, segment);

            getFeature(FeatureType.TRANSCRIPT, transID, rangeStart, rangeStop, min, segment);
            getFeature(FeatureType.TRANSCRIPT, transID, rangeStart, rangeStop, avg, segment);
            getFeature(FeatureType.TRANSCRIPT, transID, rangeStart, rangeStop, max, segment);

        }catch (Exception e){
            log.error("Error!", e);
        }
   	}

    //
    // M A I N
    //

	public static void main(String[] a){

        try {
            log.info("loading..");

            TransQuantSource source = new TransQuantSource(new FileInputStream
                    (System.getProperty("trans.quant.source")));

            log.info("loaded");
            log.info(source.getStats());

            String chr = "1";
            int start = 696291;
            int stop = 787400;

            log.info("selecting " + chr + ":" + start + "-" + stop);

            ArrayList<DataFeature> result = null;

            long startTime = System.currentTimeMillis();

            for(int i=9;i<1000;i++) {
                result = source.locateFeatures(chr, start, stop);
            }

            long estimatedTime = System.currentTimeMillis() - startTime;

            log.info("Estimated run time:" + estimatedTime + " Millis");

            log.info("result.size() = " + result.size());

            for (DataFeature f : result) {
                log.info("1 f = " + f.getId() + " start:" + f.getStart() +
                        " end:" + f.getEnd() + " score:" + f.getScore());
            }

		} catch (FileNotFoundException e) {
			//  Auto-generated catch block
			log.error("Error!", e);
		} catch (Exception e) {
			//  Auto-generated catch block
            log.error("Error!", e);
		}
	}

}
