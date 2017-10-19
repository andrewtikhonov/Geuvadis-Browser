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
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class TransEQTLSource extends GenericEQTLDataSource {

    private final static Logger log = LoggerFactory.getLogger(TransEQTLSource.class);

    public TransEQTLSource(InputStream stream) throws Exception {
        super(stream, null);
   	}

    public TransEQTLSource(InputStream stream, GenericQuantDataSource quantSource) throws Exception {
        super(stream, quantSource);
    }

    protected void processLine(String aLine, HashMap<String, DataSegment> segmentmap,
                               DataSourceInterface linkedSource) throws Exception {

        String[] parts = aLine.split("\t");

        //SNP_ID	ID	GENE_ID	PROBE_ID	CHR_SNP	CHR_GENE	SNPpos	TSSpos	distance	rvalue	pvalue	log10pvalue
        //snp_1_776546	-	ENSG00000228794.2	ENST00000415295.1	1	1	776546	762988	13558	-0.332006664423282	4.83120996635158e-11	10.3159440874884
        //snp_1_2488153	-	ENSG00000238164.2	ENST00000452793.1	1	1	2488153	2488470	317	0.306270111067703	1.54715045121136e-09	8.81046745168278
        //snp_1_2499254	-	ENSG00000238164.2	ENST00000452793.1	1	1	2499254	2488470	10784	0.305167442887428	1.7818535591185e-09	8.74912799114095
        //snp_1_2487663	-	ENSG00000238164.2	ENST00000452793.1	1	1	2487663	2488470	807	0.304622286602367	1.91031026824135e-09	8.71889608990315
        //snp_1_2475212	-	ENSG00000238164.2	ENST00000452793.1	1	1	2475212	2488470	13258	0.304405101192165	1.96395121712412e-09	8.70686930392021
        //snp_1_2507938	-	ENSG00000238164.2	ENST00000452793.1	1	1	2507938	2488470	19468	0.304025622850484	2.06120351227804e-09	8.68587912617522

        /*
        SNP_ID	ID	GENE_ID	PROBE_ID	CHR_SNP	CHR_GENE	SNPpos	TSSpos	distance	rvalue	pvalue	log10pvalue
        rs12124819	-	ENSG00000228794.2	ENST00000415295.1	1	1	776546	762988	13558	-0.332006664423282	4.83120996635158e-11	10.3159440874884
        rs4870	-	ENSG00000238164.2	ENST00000452793.1	1	1	2488153	2488470	317	0.306270111067703	1.54715045121136e-09	8.81046745168278
        rs2182176	-	ENSG00000238164.2	ENST00000452793.1	1	1	2499254	2488470	10784	0.305167442887428	1.7818535591185e-09	8.74912799114095
        rs2227312	-	ENSG00000238164.2	ENST00000452793.1	1	1	2487663	2488470	807	0.304622286602367	1.91031026824135e-09	8.71889608990315
        rs10797429	-	ENSG00000238164.2	ENST00000452793.1	1	1	2475212	2488470	13258	0.304405101192165	1.96395121712412e-09	8.70686930392021
        rs10752745	-	ENSG00000238164.2	ENST00000452793.1	1	1	2507938	2488470	19468	0.304025622850484	2.06120351227804e-09	8.68587912617522
        rs12042279	-	ENSG00000238164.2	ENST00000452793.1	1	1	2470848	2488470	17622	0.303524458773761	2.19682644237574e-09	8.65820425263611
        indel:25D_7_158664298	0	ENSG00000126870.11	ENST00000407559.3	7	7	158664310.5	158649269	15041.5	-0.3025492055	2.4859938947932e-09	8.6044999423
        */


   		if (parts.length < 12) {
            throw new Exception("Parsing Error: A line doesn't have the right number of fields [" + aLine + "]");
        }

        try {
            if ("SNP_ID".equals(parts[0])) {
                // first line, skip it
                return;
            }

            incFeaturesTotal();

            String featureId      = parts[0]; // rs12124819
            String probeId        = parts[3]; // ENST00000415295.1
            String snipSegmentId  = parts[4]; // 1
            String probeSegmentId = parts[5]; // 1
            String snpPos         = parts[6]; // 776546

            String[] trans_parts  = probeId.split("\\.");
            String transID        = trans_parts[0];


            DataSegment segment = getSegment(snipSegmentId, segmentmap);

            int rangeStart;
            int rangeEnd;

            if (featureId.startsWith("indel")) {
                // indel:25D_7_158664298
                //

                // get start for indel
                rangeStart  = Integer.parseInt(featureId.split("_")[2]);

                // parse length of indel
                int r0 = featureId.indexOf(":") + 1; // start from the next
                int r1 = featureId.indexOf("_") - 1; // cut off the letter

                // offset the end
                rangeEnd = rangeStart + Integer.parseInt(
                        featureId.substring(r0, r1)) - 1; // viewer shows 2 bars
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

            /*
            String[] parts3 = probeId.split("_");
            int probeStart = Integer.parseInt(parts3[1]);
            int probeEnd = Integer.parseInt(parts3[2]);
            */
            // FIXIT
            int probeStart = Integer.parseInt(parts[7]);
            int probeEnd   = probeStart + 1;

            //Double pvalue      = Double.parseDouble(parts[10]);
            Double log10pvalue = (double) ((int)(Double.parseDouble(parts[11]) * 1000)) / 1000;

            /*
            if (featureId.equals("rs2724384")) {
                int l = featureId.length();
                l = l + 1;
                int z = l * 2;
            }
            */

            DataFeature targetLinkedFeature = null;

            if (linkedSource != null) {
                ArrayList<DataFeature> result =
                        linkedSource.locateSegment(probeSegmentId).locateFeatureByID(transID);// probeStart, probeEnd

                if (result.size() == 0) {
                    incFeaturesNotLinked();

                    //log.error("LINKED FEATURE NOT FOUND featureId=" + featureId +
                    //          " probeSegmentId=" + probeSegmentId + " probeStart="+ probeStart + " probeId="+probeId);
                } else {
                    incFeaturesLinked();

                    // get the linked feature
                    targetLinkedFeature = result.get(0);
                }
            }

            if (targetLinkedFeature == null) {
                targetLinkedFeature = new DataFeature(FeatureType.TRANSCRIPT,
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


            TransEQTLSource source = new TransEQTLSource(
                    new FileInputStream(System.getProperty("trans.eqtl.source")));

            log.info("loaded");
            log.info(source.getStats());

            String chr = "1";
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
