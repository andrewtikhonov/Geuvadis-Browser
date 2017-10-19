package services.servlet;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.datasource.eQTL.ExonEQTLSource;
import services.datasource.eQTL.MirnaEQTLSource;
import services.datasource.eQTL.TransEQTLSource;
import services.datasource.generic.DataSourceInterface;
import services.datasource.generic.GenericEQTLDataSource;

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
 * Date: 26/11/2012
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */

public class eQTLRangeServlet extends GenericServlet implements javax.servlet.Servlet {

    final private Logger log = LoggerFactory.getLogger(getClass());

    public eQTLRangeServlet() {
   		super();
   	}

    protected String getParameterHelp() {
        return "should be ?" + ServletParameterType.RANGE + "=Y:100-500";
    }

    protected void doAny(final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            RangeParameters params = getRangeParameters(request);

            Object o = getDataSource(sourceName);

            if (o instanceof GenericEQTLDataSource) {

                ArrayList<DataFeature> list = ((GenericEQTLDataSource)o).
                        locateFeatures(params.segmentId, params.start, params.end);

                response.addHeader("Access-Control-Allow-Origin", "*");

                ServletOutputStream out = response.getOutputStream();

                Gson gson = new Gson();

                String json = gson.toJson(list);

                /*
                for(DataFeature f : list) {
                    out.println(segmentId + " " + f.getStart() + " " + f.getEnd() + " " + f.getScore() + " " + "100,0,0");
                }
                */

                out.println(json);

                out.flush();

            } else {
                throw new Exception("Data Source " + sourceName + ", not expected type " + o.getClass().toString());
            }

        } catch(Exception ex) {

            response.addHeader("Access-Control-Allow-Origin", "*");

            ServletOutputStream out = response.getOutputStream();
            out.print("[{\"error\":\"" + ex.toString() + "\"}]");
            out.flush();
        }
    }

    public DataSourceInterface createDataSource(String sourceName,
                                                String eQTLResource) throws Exception {

        InputStream stream = eQTLRangeServlet.class.getResourceAsStream(eQTLResource);

        try {

            if (sourceName.contains("mirna")) {
                return new MirnaEQTLSource(stream);

            } else if (sourceName.contains("exon")) {
                return new ExonEQTLSource(stream);

            } else if (sourceName.contains("trans")) {
                return new TransEQTLSource(stream);

            } else {
                throw new Exception(sourceName + " source name not supported");
            }

        } finally {
            stream.close();

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

                    //log.info("eQTL Range Servlet Init");

                    sourceName = final_sConfig.getInitParameter("track.name");

                    if (sourceName == null) {
                        throw new Exception("track.name parameter is not defined use <init-param> in web.xml");
                    }

                    log.info("source name " + sourceName);

                    log.info("loading properties ..");

                    initProperties();

                    String resource = getServletProperties().getProperty(sourceName);

                    log.info("loading data file from " + resource);

                    Object o = getDataSource(sourceName);

                    if (o != null) {
                        log.info("source " + sourceName + " already initialized");
                    } else {
                        log.info("initializing data source " + sourceName);

                        DataSourceInterface datareader =
                                createDataSource(sourceName, resource);

                        saveDataSource(sourceName, datareader);

                    }

                    //log.info("eQTL Range Servlet Init Completed");

                    log.info(sConfig.getServletName() + " Init Completed");


                    //try { Thread.sleep(2000); } catch (Exception ex) {}

                } catch (Exception ex) {
                    log.error("Error!", ex);
                }

        //    }
        //}).start();

   	}

}


