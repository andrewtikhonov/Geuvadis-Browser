package services.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.data.LinkedFeature;
import services.datasource.AggregatedDataSource;
import services.datasource.AggregatedSourceInterface;
import services.datasource.eQTL.ExonEQTLSource;
import services.datasource.eQTL.MirnaEQTLSource;
import services.datasource.eQTL.TransEQTLSource;
import services.datasource.generic.DataSourceInterface;
import services.datasource.generic.GenericEQTLDataSource;
import services.datasource.generic.GenericQuantDataSource;
import services.datasource.quant.ExonQuantSource;
import services.datasource.quant.MirnaQuantSource;
import services.datasource.quant.TransQuantSource;
import services.util.JsonUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 05/12/2012
 * Time: 11:50
 * To change this template use File | Settings | File Templates.
 */
public class AggregatedRangeServlet extends GenericServlet implements javax.servlet.Servlet {

    final private Logger log = LoggerFactory.getLogger(getClass());

    public AggregatedRangeServlet() {
   		super();
   	}

    protected String getParameterHelp() {
        return "should be ?" + ServletParameterType.MODE + "=<" +
                ServletParameterType.EQTL + "|" +
                ServletParameterType.QUANT + ">&" +
                ServletParameterType.RANGE + "=Y:100-500";
    }

    private eQTLObjectWriter   eQTLWriter = new eQTLObjectWriter();
    private LinkedObjectWriter linkedWriter = new LinkedObjectWriter();
    private QuantObjectWriter  quantWriter = new QuantObjectWriter();

    private class eQTLObjectWriter implements JsonUtil.JsonObjectWriter {
        public void writeItem(Object o, ServletOutputStream output) throws Exception {

            if (o instanceof DataFeature) {
                DataFeature df = (DataFeature) o;

                output.print(JsonUtil.toJson(ServletDataType.CHR,    df.getChr()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.ID,     df.getId()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.START,  Integer.toString(df.getStart())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.END,    Integer.toString(df.getEnd())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.TYPE,   df.getType()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.SCORE,  Double.toString(df.getScore())) + ",");
                output.print('"' + ServletDataType.LINKED + '"' + ':');

                JsonUtil.writeJsonList(df.getLinked(), linkedWriter, output);
            }
        }
    }

    private class LinkedObjectWriter implements JsonUtil.JsonObjectWriter {
        public void writeItem(Object o, ServletOutputStream output) throws Exception {

            if (o instanceof LinkedFeature) {
                LinkedFeature lf = (LinkedFeature) o;

                output.print(JsonUtil.toJson(ServletDataType.LINKSCORE,  Double.toString(lf.getLinkScore())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.BEST,       Boolean.toString(lf.isBest())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.ID,         lf.getFeature().getId()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.CHR,        lf.getFeature().getChr()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.START,      Integer.toString(lf.getFeature().getStart())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.END,       Integer.toString(lf.getFeature().getEnd())));
            }
        }
    }

    private class QuantObjectWriter implements JsonUtil.JsonObjectWriter {
        public void writeItem(Object o, ServletOutputStream output) throws Exception {

            if (o instanceof DataFeature) {
                DataFeature df = (DataFeature) o;

                output.print(JsonUtil.toJson(ServletDataType.CHR,    df.getChr()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.ID,     df.getId()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.START,  Integer.toString(df.getStart())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.END,    Integer.toString(df.getEnd())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.TYPE,   df.getType()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.SCORE,  Double.toString(df.getScore())));
            }
        }
    }


    protected void doAny(final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            response.addHeader("Access-Control-Allow-Origin", "*");

            RangeParameters params = getRangeParameters(request);
            String mode = getModeParameter(request);

            Object o = getDataSource(sourceName);

            if (o instanceof AggregatedDataSource) {

                if (mode.equalsIgnoreCase(ServletParameterType.EQTL)) {

                    ArrayList<DataFeature> list = ((AggregatedDataSource)o).
                            locateEQTLFeatures(params.segmentId, params.start, params.end);

                    ServletOutputStream out = response.getOutputStream();

                    JsonUtil.writeJsonList(list, eQTLWriter, out);

                    out.flush();
                }

                if (mode.equalsIgnoreCase(ServletParameterType.QUANT)) {

                    ArrayList<DataFeature> list = ((AggregatedDataSource)o).
                            locateQuantFeatures(params.segmentId, params.start, params.end);


                    ServletOutputStream out = response.getOutputStream();

                    JsonUtil.writeJsonList(list, quantWriter, out);

                    out.flush();
                }

            } else {
                throw new Exception("Data Source " + sourceName + ", not expected type " + o.getClass().toString());
            }

        } catch(Exception ex) {

            ServletOutputStream out = response.getOutputStream();
            out.print("[{\"error\":\"" + ex.toString() + "\"}]");
            out.flush();
        }
    }

    public AggregatedSourceInterface createDataSource(String sourceName,
                                                      String quantResource,
                                                      String eQTLResource) throws Exception {
        InputStream  qStream = AggregatedRangeServlet.class.getResourceAsStream(quantResource);
        InputStream  eStream = AggregatedRangeServlet.class.getResourceAsStream(eQTLResource);

        try {

            AggregatedDataSource source = null;

            if (sourceName.contains("mirna")) {
                MirnaQuantSource qSource = new MirnaQuantSource(qStream);
                MirnaEQTLSource  eSource = new MirnaEQTLSource(eStream, qSource);

                source = new AggregatedDataSource(qSource, eSource);

            } else if (sourceName.contains("exon")) {
                ExonQuantSource  qSource = new ExonQuantSource(qStream);
                ExonEQTLSource   eSource = new ExonEQTLSource(eStream, qSource);

                source = new AggregatedDataSource(qSource, eSource);

            } else if (sourceName.contains("trans")) {
                TransQuantSource qSource = new TransQuantSource(qStream);
                TransEQTLSource  eSource = new TransEQTLSource(eStream, qSource);

                source = new AggregatedDataSource(qSource, eSource);

            } else {
                throw new Exception(sourceName + " source name not supported");
            }

            log.info("Data source: " + sourceName);
            log.info(" " + ((GenericQuantDataSource) source.quantSource).getStats());
            log.info(" " + ((GenericEQTLDataSource) source.eQTLSource).getStats());

            if( ((GenericEQTLDataSource) source.eQTLSource).getFeaturesNotLinked() != 0) {
                log.warn("NOT LINKED FEATUES " + ((GenericEQTLDataSource) source.eQTLSource).getFeaturesNotLinked() +
                        " OUT OF " + ((GenericEQTLDataSource) source.eQTLSource).getFeaturesTotal());
            }

            return source;

        }  finally {
            qStream.close();
            eStream.close();

            System.gc();
        }

    }

    public void init(ServletConfig sConfig) throws ServletException {
   		super.init(sConfig);

        final ServletConfig final_sConfig = sConfig;

        //new Thread(new Runnable() {
        //    public void run() {
                //To change body of implemented methods use File | Settings | File Templates.

                try {
                    log.info(sConfig.getServletName() + " Init");

                    sourceName = final_sConfig.getInitParameter("source.name");

                    if (sourceName == null) {
                        throw new Exception("source.name parameter is not defined use <init-param> in web.xml");
                    }

                    String quantTrack = sourceName + ".quant";
                    String eQTLTrack = sourceName + ".eqtl";

                    log.info("source name " + sourceName);
                    log.info("   quant track " + quantTrack);
                    log.info("   eQTL track " + eQTLTrack);

                    log.info("loading properties ..");

                    initProperties();

                    String quantResource = getServletProperties().getProperty(quantTrack);
                    String eQTLResource = getServletProperties().getProperty(eQTLTrack);

                    log.info("quant data file " + quantResource);
                    log.info("eQTL data file " + eQTLResource);

                    Object o = getDataSource(sourceName);

                    if (o != null) {
                        log.info("source " + sourceName + " already initialized");
                    } else {
                        log.info("initializing data source " + sourceName);

                        AggregatedSourceInterface datareader =
                                createDataSource(sourceName, quantResource, eQTLResource);

                        saveDataSource(sourceName, datareader);

                    }

                    log.info(sConfig.getServletName() + " Init Completed");

                    //try { Thread.sleep(2000); } catch (Exception ex) {}

                } catch (Exception ex) {
                    log.error("Error!", ex);
                }

        //    }
        //}).start();
   	}

}
