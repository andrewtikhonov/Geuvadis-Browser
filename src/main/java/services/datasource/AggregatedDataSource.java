package services.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.data.LinkedFeature;
import services.datasource.eQTL.ExonEQTLSource;
import services.datasource.eQTL.TransEQTLSource;
import services.datasource.generic.DataSourceInterface;
import services.datasource.generic.GenericEQTLDataSource;
import services.datasource.generic.GenericQuantDataSource;
import services.datasource.quant.ExonQuantSource;
import services.datasource.quant.TransQuantSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 04/12/2012
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class AggregatedDataSource implements AggregatedSourceInterface {

    private final static Logger log = LoggerFactory.getLogger(AggregatedDataSource.class);

    public DataSourceInterface quantSource;
    public DataSourceInterface eQTLSource;

	public AggregatedDataSource(DataSourceInterface quantSource, DataSourceInterface eQTLSource) throws Exception {
        this.quantSource = quantSource;
        this.eQTLSource = eQTLSource;
	}

    //
    // G E T T E R S
    //
    public ArrayList<DataFeature> locateQuantFeatures(String segmentId, int start, int stop) {
        return quantSource.locateSegment(segmentId).locateFeatures(start, stop);
   	}

    public ArrayList<DataFeature> locateEQTLFeatures(String segmentId, int start, int stop) {
        return eQTLSource.locateSegment(segmentId).locateFeatures(start, stop);
   	}

    public ArrayList<DataFeature> locateQuantFeatureByID(String featureId) {
        return quantSource.locateFeatureByID(featureId);
    }

    public ArrayList<DataFeature> locateEQTLFeatureByID(String featureId) {
        return eQTLSource.locateFeatureByID(featureId);
    }

    public ArrayList<DataFeature> locateQuantFeatureByID(String featureId, String segmentId) {
        return quantSource.locateFeatureByID(featureId, segmentId);
    }

    public ArrayList<DataFeature> locateEQTLFeatureByID(String featureId, String segmentId) {
        return eQTLSource.locateFeatureByID(featureId, segmentId);
    }

    //
    // M A I N
    //

	public static void main(String[] a){

        try {
            log.info("loading..");

            //ExonQuantSource qs = new ExonQuantSource(new FileInputStream(System.getProperty("exon.quant.source")));
            //ExonEQTLSource  es = new ExonEQTLSource(new FileInputStream(System.getProperty("exon.eqtl.source")), qs);

            TransQuantSource qs = new TransQuantSource(new FileInputStream(System.getProperty("exon.quant.source")));
            TransEQTLSource  es = new TransEQTLSource(new FileInputStream(System.getProperty("exon.eqtl.source")), qs);


            AggregatedDataSource aggregatedSource = new AggregatedDataSource(qs, es);

            log.info("loaded");

            log.info(" " + ((GenericQuantDataSource)aggregatedSource.quantSource).getStats());
            log.info(" " + ((GenericEQTLDataSource)aggregatedSource.eQTLSource).getStats());

            String chr = "1";
            int start = 1628906;
            int stop  = 1629906;

            log.info("selecting " + chr + ":" + start + "-" + stop);

            ArrayList<DataFeature> quantResult = null;
            ArrayList<DataFeature> eQTLResult = null;

            long startTime = System.currentTimeMillis();

            quantResult = aggregatedSource.locateQuantFeatures(chr, start, stop);
            eQTLResult = aggregatedSource.locateEQTLFeatures(chr, start, stop);

            //for(int i=9;i<1000;i++) {
            //}

            long estimatedTime = System.currentTimeMillis() - startTime;

            log.info("Estimated run time:" + estimatedTime + " Millis");

            log.info("quantResult.size() = " + quantResult.size());
            log.info("eQTLResult.size() = " + eQTLResult.size());

            for (DataFeature f : quantResult) {
                log.info("1 f = " + f.getId() + " start:" + f.getStart() +
                        " end:" + f.getEnd() + " score:" + f.getScore() );
            }

            for (DataFeature f : eQTLResult) {
                log.info("f = " + f.getId() + " start:" + f.getStart() +
                        " end:" + f.getEnd() + " score:" + f.getScore());

                log.info("linked to :");

                for (LinkedFeature l0 : f.getLinked()) {
                    log.info("      linked = " + l0.getFeature().getId() + " start:" + l0.getFeature().getStart() +
                                                " end:" + l0.getFeature().getEnd() + " link score:" + l0.getLinkScore());
                }
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
