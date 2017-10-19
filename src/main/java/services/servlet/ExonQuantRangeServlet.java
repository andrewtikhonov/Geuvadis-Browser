package services.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.data.DataFeature;
import services.datasource.generic.DataSourceInterface;
import services.datasource.generic.GenericQuantDataSource;
import services.datasource.quant.ExonQuantSource;
import services.datasource.quant.MirnaQuantSource;
import services.datasource.quant.TransQuantSource;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class ExonQuantRangeServlet extends GenericServlet implements javax.servlet.Servlet {

    final private Logger log = LoggerFactory.getLogger(getClass());

    public ExonQuantRangeServlet() {
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

            if (o instanceof GenericQuantDataSource) {

                ArrayList<DataFeature> list = ((GenericQuantDataSource)o).
                        locateFeatures(params.segmentId, params.start, params.end);

                response.addHeader("Access-Control-Allow-Origin", "*");


                ServletOutputStream out = response.getOutputStream();


                for(DataFeature f : list) {
                    out.println(params.segmentId + " " +
                            f.getStart() + " " + f.getEnd() + " " +
                            f.getScore() + " " + f.getId());
                }

                out.flush();

            } else {
                throw new Exception("Data Source " + sourceName +
                        ", not expected type " + o.getClass().toString());
            }

        } catch(Exception ex) {

            response.addHeader("Access-Control-Allow-Origin", "*");

            ServletOutputStream out = response.getOutputStream();
            out.print("[{\"error\":\"" + ex.toString() + "\"}]");
            out.flush();
        }
    }

    public DataSourceInterface createDataSource(String sourceName,
                                                String resource) throws Exception {
        InputStream stream = ExonQuantRangeServlet.class.getResourceAsStream(resource);

        try {

            if (sourceName.contains("mirna")) {
                return new MirnaQuantSource(stream);

            } else if (sourceName.contains("exon")) {
                return new ExonQuantSource(stream);

            } else if (sourceName.contains("trans")) {
                return new TransQuantSource(stream);

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
                    //log.info("Exon Quantification Servlet Init");

                    sourceName = final_sConfig.getInitParameter("track.name");

                    if (sourceName == null) {
                        throw new Exception("track.name parameter is not defined use <init-param> in web.xml");
                    }

                    log.info("track name " + sourceName);

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

                    //log.info("Exon Quantification Servlet Init Completed");
                    log.info(sConfig.getServletName() + " Init Completed");


                    //try { Thread.sleep(2000); } catch (Exception ex) {}

                } catch (Exception ex) {
                    log.error("Error!", ex);
                }

        //    }
        //}).start();

   	}

}

