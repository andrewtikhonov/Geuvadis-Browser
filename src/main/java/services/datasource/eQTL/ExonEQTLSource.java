package services.datasource.eQTL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.data.DataSegment;
import services.data.FeatureType;
import services.data.LinkedFeature;
import services.datasource.generic.DataSourceInterface;
import services.datasource.generic.GenericDataSource;
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
public class ExonEQTLSource extends GenericEQTLDataSource {

    private final static Logger log = LoggerFactory.getLogger(ExonEQTLSource.class);

    public ExonEQTLSource(InputStream stream) throws Exception {
        super(stream, null);
   	}

    public ExonEQTLSource(InputStream stream, GenericQuantDataSource quantSource) throws Exception {
        super(stream, quantSource);
    }

    protected void processLine(String aLine, HashMap<String, DataSegment> segmentmap,
                               DataSourceInterface linkedSource) throws Exception {

   	    String[] parts = aLine.split("\t");

        /*

        // old format

        SNP_ID	ID	GENE_ID	PROBE_ID	CHR_SNP	CHR_GENE	SNPpos	TSSpos	distance	rvalue	pvalue	log10pvalue
        snp_1_1651266	-	ENSG00000008128.15	ENSG00000008128.15_1636343_1636464	1	1	1651266	1655966	4700	0.812696694297885	7.84962849298652e-22	21.1051508970463
        snp_1_1638187	-	ENSG00000008128.15	ENSG00000008128.15_1636343_1636464	1	1	1638187	1655966	17779	-0.795218818793282	2.3561411821894e-20	19.6277986897726
        snp_1_1651727	-	ENSG00000008128.15	ENSG00000008128.15_1634915_1635063	1	1	1651727	1655966	4239	0.791850760268475	4.3717321621667e-20	19.3593464533226
        snp_1_1635928	-	ENSG00000008128.15	ENSG00000008128.15_1636343_1636464	1	1	1635928	1655966	20038	-0.786718745467259	1.09757210488553e-19	18.9595669392177
        snp_1_1651266	-	ENSG00000008128.15	ENSG00000008128.15_1637078_1637171	1	1	1651266	1655966	4700	0.785813257089381	1.28778045589286e-19	18.8901581703002

        */

        /*

        //new updated format

        SNP_ID	ID	GENE_ID	PROBE_ID	CHR_SNP	CHR_GENE	SNPpos	TSSpos	distance	rvalue	pvalue	log10pvalue
        rs61776804	-	ENSG00000008128.15	ENSG00000008128.15_1635989_1636094	1	1	1628906	1655966	27060	-0.82697990607744	2.12608060129181e-94	93.6724202750748
        rs601141	-	ENSG00000008128.15	ENSG00000008128.15_1635989_1636094	1	1	1627805	1655966	28161	-0.809067191690805	2.67838656568429e-87	86.5721267420261
        rs138271907	-	ENSG00000008128.15	ENSG00000008128.15_1635989_1636094	1	1	1627556	1655966	28410	-0.808757011683901	3.50078260086576e-87	86.4558348581512
        rs2076329	-	ENSG00000008128.15	ENSG00000008128.15_1635989_1636094	1	1	1635619	1655966	20347	-0.808538924716515	4.22476660168763e-87	86.3741972787614
        rs874516	-	ENSG00000008128.15	ENSG00000008128.15_1635989_1636094	1	1	1635004	1655966	20962	-0.807587150516352	9.56893727966696e-87	86.0191362920251

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

            String featureId      = parts[0]; // rs61776804
            String probeId        = parts[3]; // ENSG00000008128.15_1635989_1636094
            String snipSegmentId  = parts[4]; // 1
            String probeSegmentId = parts[5]; // 1
            String snpPos         = parts[6]; // 1628906

            // get segment
            DataSegment segment = getSegment(snipSegmentId, segmentmap);

            int rangeStart;
            int rangeEnd;

            // check if it's an indel
            if (featureId.startsWith("indel")) {
                // indel looks like
                // indel:6D_5_159913283
                // indel:4I_1_1465688

                // value for start in SNPpos is somewhat incorrect
                // use the 159913283 from indel:6D_5_159913283

                rangeStart  = Integer.parseInt( featureId.split("_")[2] );

                // extract "6" from "6D" to get the length of the indel
                // indel:4D_1_1656100

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

            // extract the reference
            String[] parts3 = probeId.split("_");
            int probeStart  = Integer.parseInt( parts3[1] );
            int probeEnd    = Integer.parseInt( parts3[2] );

            Double rho         = Double.parseDouble(parts[9]);
            Double pvalue      = Double.parseDouble(parts[10]);
            Double log10pvalue = (double) ((int)(Double.parseDouble(parts[11]) * 1000)) / 1000;

            DataFeature targetLinkedFeature = null;

            if (linkedSource != null) {
                targetLinkedFeature =
                        linkedSource.locateSegment(probeSegmentId).locateOneFeature(probeStart, probeEnd);

                if (targetLinkedFeature == null) {
                    incFeaturesNotLinked();

                    //log.error("LINKED FEATURE NOT FOUND featureId=" + featureId +
                    //        " probeSegmentId=" + probeSegmentId + " probeStart=" + probeStart);
                } else {
                    incFeaturesLinked();

                }
            }

            if (targetLinkedFeature == null) {
                targetLinkedFeature = new DataFeature(FeatureType.EXON,
                        probeId, probeSegmentId, probeStart, probeEnd, log10pvalue, rho, pvalue);
            }

            DataFeature newFeature = getFeature(getFeatureType(featureId), featureId,
                    rangeStart, rangeEnd, log10pvalue, rho, pvalue, segment);

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

            ExonEQTLSource source = new ExonEQTLSource(new FileInputStream(
                    System.getProperty("exon.eqtl.source")));

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
