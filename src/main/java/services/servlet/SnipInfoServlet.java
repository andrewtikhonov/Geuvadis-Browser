package services.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.datasource.AggregatedSourceInterface;
import services.util.JsonUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 01/02/2013
 * Time: 14:52
 * To change this template use File | Settings | File Templates.
 */
public class SnipInfoServlet extends GenericServlet implements javax.servlet.Servlet {

    final private Logger log = LoggerFactory.getLogger(getClass());

    private String[] snipSourceNames = null;

    public SnipInfoServlet() {
   		super();
   	}

    private class SnipObjectWriter implements JsonUtil.JsonObjectWriter {
        public void writeItem(Object o, ServletOutputStream output) throws Exception {

            if (o instanceof DataFeature) {
                DataFeature f = (DataFeature) o;

                output.print(JsonUtil.toJson(ServletDataType.ID,    f.getId()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.CHR,   f.getChr()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.TYPE,  f.getType()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.START, Integer.toString(f.getStart())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.END,   Integer.toString(f.getEnd())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.SCORE, Double.toString(f.getScore())));
            }
        }
    }

    protected String getParameterHelp() {
        return "should be info?snip=snp_20_631139";
    }

   	protected void doGet(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
        try {
            doAny(request, response);
        } catch (Exception ex) {
            log.error("Unexpected Exception ", ex);
        }
   	}

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws javax.servlet.ServletException, java.io.IOException {
        try {
            doAny(request, response);
        } catch (Exception ex) {
            log.error("Unexpected Exception ", ex);
        }
    }

    protected String getSegmentIDFromSnipID(String snipid) {
        return snipid.split("_")[1];
    }

    protected String toJson(String key, String value) {
        return '"' + key + '"'+ ':' + (value == null ? "null" : ( '"' + value + '"'));
    }

    protected void doAny(final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            response.addHeader("Access-Control-Allow-Origin", "*");

            final String parameter = request.getParameter("snip");

            if (parameter == null) {
                new Exception("No parameter found, " + getParameterHelp());
            }

            //String segmentId = getSegmentIDFromSnipID(parameter);

            Vector<Object> sources = getEQTLDataSources(snipSourceNames);

            for (Object o : sources) {

                if (o instanceof AggregatedSourceInterface) {

                    log.info("locating " + parameter + " in " + sourceName);

                    //public DataFeature            locateQuantFeatureByID(String featureId);
                    //public DataFeature            locateEQTLFeatureByID(String featureId);
                    //public DataFeature            locateQuantFeatureByID(String featureId, String segmentId);
                    //public DataFeature            locateEQTLFeatureByID(String featureId, String segmentId);

                    ArrayList<DataFeature> fl = ((AggregatedSourceInterface)o).
                            locateEQTLFeatureByID(parameter); // segmentId

                    if (fl.size() > 0) {

                        ServletOutputStream out = response.getOutputStream();

                        SnipObjectWriter writer = new SnipObjectWriter();

                        JsonUtil.writeJsonList(fl, writer, out);

                        out.flush();

                        return;
                    }

                } else {

                }
            }

            throw new Exception("snip " + parameter + " not found");

        } catch(Exception ex) {

            ServletOutputStream out = response.getOutputStream();
            out.print("[{\"error\":\"" + ex.getMessage() + "\"}]");
            out.flush();
        }
    }

    public Vector<Object> getEQTLDataSources(String[] sNames) throws Exception {
        Vector<Object> sourceObjects = new Vector<Object>();

        for (String sn : sNames) {
            sourceObjects.add(getSourceMap().get(sn));
        }

        return sourceObjects;
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

                    log.info("source name " + sourceName);

                    log.info("loading properties ..");

                    initProperties();

                    snipSourceNames = getServletProperties().
                            getProperty(sourceName).split(";");

                    log.info(sConfig.getServletName() + " Init Completed");

                    //try { Thread.sleep(2000); } catch (Exception ex) {}

                } catch (Exception ex) {
                    log.error("Error!", ex);
                }

        //    }
        //}).start();

   	}


}

