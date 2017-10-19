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
 * Time: 10:42
 * To change this template use File | Settings | File Templates.
 */
public class MirnaQuantSource extends GenericQuantDataSource {

    private final static Logger log = LoggerFactory.getLogger(ExonQuantSource.class);

    public MirnaQuantSource(InputStream stream) throws Exception {
        super(stream);
   	}

    protected void processLine(String aLine, HashMap<String, DataSegment> segmentmap,
                               DataSourceInterface linkedSource) throws Exception {

   	    String[] parts = aLine.split("\t");

        //hsa-miR-200b-3p_1102540_1102561	hsa-miR-200b-3p	1	1102540	-8.8551516917041	24.871	88.74154889439
        //hsa-miR-200a-3p_1103296_1103317	hsa-miR-200a-3p	1	1103296	-3.5237597782919	5.758	18.5284695290662
        //hsa-miR-429-3p_1104435_1104456	hsa-miR-429-3p	1	1104435	-0.425024000502213	2.486	9.66626565005824
        //hsa-miR-378f-3p_24255611_24255630	hsa-miR-378f-3p	1	24255611	-1.24831514367982	4.906	25.1716406206717
        //hsa-miR-4425-3p_25350043_25350064	hsa-miR-4425-3p	1	25350043	-0.938046309173874	4.861	12.1549407992825

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

        String mirnaID     = parts2[0];

        //String[] parts3  = exonID.split("\\.");
        //String geneID    = parts3[0];

        try {
            int rangeStart = Integer.parseInt(parts2[1]);
            int rangeStop  = Integer.parseInt(parts2[2]);

            Double min    = Double.parseDouble(parts[4]) + 20;
            Double avg    = Double.parseDouble(parts[5]) + 20;
            Double max    = Double.parseDouble(parts[6]) + 20;

            //getFeature(targetID + "_MIN", rangeStart, rangeStop, min, segment);
            //getFeature(targetID + "_AVG", rangeStart, rangeStop, avg, segment);
            //getFeature(targetID + "_MAX", rangeStart, rangeStop, max, segment);

            getFeature(FeatureType.MIRNA, mirnaID, rangeStart, rangeStop, min, segment);
            getFeature(FeatureType.MIRNA, mirnaID, rangeStart, rangeStop, avg, segment);
            getFeature(FeatureType.MIRNA, mirnaID, rangeStart, rangeStop, max, segment);

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


            MirnaQuantSource source = new MirnaQuantSource(
                    new FileInputStream(System.getProperty("mirna.quant.source")));

            log.info("loaded");
            log.info(source.getStats());

            String chr = "1";
            int start = 696291;
            int stop = 1787400;

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
