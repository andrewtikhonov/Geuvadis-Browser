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
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class ExonQuantSource extends GenericQuantDataSource {

    private final static Logger log = LoggerFactory.getLogger(ExonQuantSource.class);

    public ExonQuantSource(InputStream stream) throws Exception {
        super(stream);
   	}

    protected void processLine(String aLine, HashMap<String, DataSegment> segmentmap,
                               DataSourceInterface linkedSource) throws Exception {

        String[] parts = aLine.split("\t");

        //ENSG00000000003.9_99883667_99884983	ENSG00000000003.9	X	99894988	0	0.292	2.598
        //ENSG00000000003.9_99885756_99885863	ENSG00000000003.9	X	99894988	0	0.733	13.193
        //ENSG00000000003.9_99887482_99887565	ENSG00000000003.9	X	99894988	0	0.489	5.669
        //ENSG00000000003.9_99888402_99888536	ENSG00000000003.9	X	99894988	0	0.530	4.689

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
        String geneID    = parts3[0];

        try {
            int rangeStart = Integer.parseInt(parts2[1]);
            int rangeStop  = Integer.parseInt(parts2[2]);

            Double min    = Double.parseDouble(parts[4]);
            Double avg    = Double.parseDouble(parts[5]);
            Double max    = Double.parseDouble(parts[6]);

            //getFeature(targetID + "_MIN", rangeStart, rangeStop, min, segment);
            //getFeature(targetID + "_AVG", rangeStart, rangeStop, avg, segment);
            //getFeature(targetID + "_MAX", rangeStart, rangeStop, max, segment);

            getFeature(FeatureType.EXON, geneID, rangeStart, rangeStop, min, segment);
            getFeature(FeatureType.EXON, geneID, rangeStart, rangeStop, avg, segment);
            getFeature(FeatureType.EXON, geneID, rangeStart, rangeStop, max, segment);

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

            ExonQuantSource source = new ExonQuantSource(
                    new FileInputStream(System.getProperty("exon.quant.source")));

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
