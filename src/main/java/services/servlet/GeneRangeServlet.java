package services.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.GeneFeature;
import services.datasource.GeneDataSource;
import services.util.JsonUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 28/11/2012
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
public class GeneRangeServlet extends GenericServlet implements javax.servlet.Servlet {

    final private Logger log = LoggerFactory.getLogger(getClass());

    public GeneRangeServlet() {
   		super();
   	}

    protected String getParameterHelp() {
        return "should be ?" + ServletParameterType.RANGE + "=Y:100-500";
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

    private class GeneObjectWriter implements JsonUtil.JsonObjectWriter {
        public void writeItem(Object o, ServletOutputStream output) throws Exception {

            if (o instanceof GeneFeature) {
                GeneFeature g = (GeneFeature) o;

                output.print(JsonUtil.toJson(ServletDataType.NAME, g.getDisplayName()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.ID, g.getStableID()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.BIOTYPE, g.getBiotype()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.DESCRIPTION, g.getDescription()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.CHR, g.getChr()) + ",");
                output.print(JsonUtil.toJson(ServletDataType.START, Integer.toString(g.getStart())) + ",");
                output.print(JsonUtil.toJson(ServletDataType.END, Integer.toString(g.getEnd())));

            }
        }
    }

    protected void doAny(final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            response.addHeader("Access-Control-Allow-Origin", "*");

            RangeParameters params = getRangeParameters(request);

            Object o = getDataSource(sourceName);

            if (o instanceof GeneDataSource) {

                ArrayList<GeneFeature> list = ((GeneDataSource)o).
                        locateFeatures(params.segmentId, params.start, params.end);

                ServletOutputStream out = response.getOutputStream();

                GeneObjectWriter writer = new GeneObjectWriter();

                JsonUtil.writeJsonList(list, writer, out);

                out.flush();

            } else {
                throw new Exception("Data Source " + sourceName +
                        ", not expected type " + o.getClass().toString());
            }

        } catch(Exception ex) {

            ServletOutputStream out = response.getOutputStream();
            out.print("[{\"error\":\"" + ex.toString() + "\"}]");
            out.flush();
        }
    }

    public void init(ServletConfig sConfig) throws ServletException {
   		super.init(sConfig);

        try {
            log.info(sConfig.getServletName() + " Init");

            sourceName = sConfig.getInitParameter("track.name");

            if (sourceName == null) {
                throw new Exception("track.name parameter is not defined use <init-param> in web.xml");
            }

            log.info("track name " + sourceName);

            Object o = getDataSource(sourceName);

            if (o != null) {
                log.info("source " + sourceName + " already initialized");
            } else {
                log.info("initializing data source " + sourceName);

                log.info("loading properties ..");

                initProperties();

                String resource = getServletProperties().getProperty(sourceName);

                log.info("loading data file from " + resource);

                GeneDataSource datareader = new GeneDataSource(GeneInfoServlet
                        .class.getResourceAsStream(resource));

                saveDataSource(sourceName, datareader);
            }

            log.info(sConfig.getServletName() + " Init Completed");

        } catch (Exception ex) {
            log.error("Error!", ex);
        }


   	}

}
