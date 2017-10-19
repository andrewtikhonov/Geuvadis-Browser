package services.datasource.eQTL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.data.DataSegment;
import services.data.FeatureType;
import services.data.LinkedFeature;
import services.datasource.generic.DataSourceInterface;
import services.datasource.generic.GenericEQTLDataSource;
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
public class MirnaEQTLSource extends GenericEQTLDataSource {

    private final static Logger log = LoggerFactory.getLogger(MirnaEQTLSource.class);

    public MirnaEQTLSource(InputStream stream) throws Exception {
        super(stream, null);
   	}

    public MirnaEQTLSource(InputStream stream, GenericQuantDataSource quantSource) throws Exception {
        super(stream, quantSource);
    }

    protected void processLine(String aLine, HashMap<String, DataSegment> segmentmap,
                               DataSourceInterface linkedSource) throws Exception {

        String[] parts = aLine.split("\t");

        //SNP_ID	ID	GENE_ID	PROBE_ID	CHR_SNP	CHR_GENE	SNPpos	TSSpos	distance	rvalue	pvalue	log10pvalue
        //indel:6D_5_159913283	-	hsa-miR-146a-3p	hsa-miR-146a-3p	5	5	159913286	159912415	871	0.71908455728829	2.90082705982913e-15	14.5374781619939
        //snp_5_159914665	-	hsa-miR-146a-3p	hsa-miR-146a-3p	5	5	159914665	159912415	2250	0.704221626215488	1.84760762922441e-14	13.7333902531135
        //snp_5_159912418	-	hsa-miR-146a-3p	hsa-miR-146a-3p	5	5	159912418	159912415	3	-0.70142088195568	2.58619584687278e-14	13.5873385900529
        //snp_5_159914240	-	hsa-miR-146a-3p	hsa-miR-146a-3p	5	5	159914240	159912415	1825	0.701179476468842	2.66177935307164e-14	13.574827948003

        /*
        SNP_ID	ID	GENE_ID	PROBE_ID	CHR_SNP	CHR_GENE	SNPpos	TSSpos	distance	rvalue	pvalue	log10pvalue
        rs11583992	-	hsa-miR-200b-3p	hsa-miR-200b-3p	1	1	1088671	1102540	13869	0.330060158180275	1.14581318392187e-10	9.9408861849913
        rs11584885	-	hsa-miR-200b-3p	hsa-miR-200b-3p	1	1	1099437	1102540	3103	0.326066080184693	1.96825554428368e-10	9.70591851654113
        rs61768478	-	hsa-miR-200b-3p	hsa-miR-200b-3p	1	1	1097291	1102540	5249	0.321917958966141	3.42385763732593e-10	9.46548430141578
        rs113657720	-	hsa-miR-200b-3p	hsa-miR-200b-3p	1	1	1092367	1102540	10173	0.318775511840576	5.17892256316596e-10	9.28576058263747
        */

   		if (parts.length < 12) {
            throw new Exception("Parsing Error: A line doesn't have the right number of fields [" + aLine + "]");
        }

        try {
            if ("SNP_ID".equals(parts[0])) {
                // first line, skip it
                return;
            }

            String featureId      = parts[0];  // rs11583992
            String probeId        = parts[3];  // hsa-miR-200b-3p
            String snipSegmentId  = parts[4];  // 5
            String probeSegmentId = parts[5];  // 5
            String snpPos         = parts[6];  // 1088671

            DataSegment segment = getSegment(snipSegmentId, segmentmap);

            int rangeStart;
            int rangeEnd;

            if (featureId.startsWith("indel")) {

                // take start from the id
                // because SNPpos in case of indel contains
                // median value, not the start of an indel
                rangeStart = Integer.parseInt( featureId.split("_")[2] );

                int r0 = featureId.indexOf(":") + 1; // start from the next
                int r1 = featureId.indexOf("_") - 1; // cut off the letter

                rangeEnd = rangeStart + Integer.parseInt(
                        featureId.substring(r0, r1)) - 1; // viewer shows 2 bars
                                                          // if end is 1 bigger than start
            } else {
                try {
                    rangeStart  = Integer.parseInt(snpPos);

                } catch (Exception ex) {
                    int i = snpPos.indexOf(".");
                    rangeStart = Integer.parseInt(snpPos.substring(0, i));
                }

                rangeEnd    = rangeStart;   // assume it's a SNP
                                            // viewer shows 2 bars if end is 1 bigger than start
            }

            //Double pvalue      = Double.parseDouble(parts[10]);
            Double log10pvalue = (double) ((int)(Double.parseDouble(parts[11]) * 1000)) / 1000;

            DataFeature targetLinkedFeature = null;

            //String[] parts3 = probeId.split("_");
            int probeStart = Integer.parseInt(parts[7]); // 159912415
            int probeEnd = probeStart + 1; // Integer.parseInt(parts3[2]);

            if (linkedSource != null) {
                targetLinkedFeature =
                        linkedSource.locateSegment(probeSegmentId).locateOneFeature(probeStart, probeEnd);

                if (targetLinkedFeature == null) {
                    //log.error("LINKED FEATURE NOT FOUND featureId=" + featureId +
                    //        " probeSegmentId=" + probeSegmentId);
                }
            }

            if (targetLinkedFeature == null) {
                targetLinkedFeature = new DataFeature(FeatureType.MIRNA,
                        probeId, probeSegmentId, probeStart, probeEnd, log10pvalue);
            }

            DataFeature newFeature = getFeature(getFeatureType(featureId), featureId,
                    rangeStart, rangeEnd, log10pvalue, segment);

            newFeature.getLinked().add(new LinkedFeature(log10pvalue, targetLinkedFeature));

            if (log10pvalue > newFeature.getScore()) {
                newFeature.setScore(log10pvalue);
            }

        } catch (Exception e){
            log.error("Error!", e);
        }
   	}


    //
    // M A I N
    //

	public static void main(String[] a){

        try {
            log.info("loading..");


            MirnaEQTLSource source = new MirnaEQTLSource(new FileInputStream(
                    System.getProperty("mirna.eqtl.source")));

            log.info("loaded");
            log.info(source.getStats());

            String chr = "7";
            int start = 1628906;
            int stop  = 1629906;

            log.info("selecting " + chr + ":" + start + "-" + stop);

            ArrayList<DataFeature> result = null;

            long startTime = System.currentTimeMillis();

            result = source.locateFeatures(chr, start, stop);

            //for(int i=9;i<1000;i++) {
            //}

            long estimatedTime = System.currentTimeMillis() - startTime;

            log.info("Estimated run time:" + estimatedTime + " Millis");

            log.info("result.size() = " + result.size());

            for (DataFeature f : result) {
                log.info("1 f = " + f.getId() + " start:" + f.getStart() +
                        " end:" + f.getEnd() + " score:" + f.getScore() + " linked: " + f.getLinked() );
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
