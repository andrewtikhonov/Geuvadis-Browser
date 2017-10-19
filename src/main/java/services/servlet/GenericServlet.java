package services.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 28/11/2012
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */
public abstract class GenericServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

    final private Logger log = LoggerFactory.getLogger(getClass());

    protected String sourceName = "UNDEF";

    public GenericServlet() {
   		super();
   	}

    protected abstract String getParameterHelp();

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

    protected abstract void doAny(final HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

    private void injectSystemProperties(InputStream is, boolean override) {
   		if (is != null) {
   			try {
   				Properties props = new Properties();

   				props.loadFromXML(is);

                   //log.info("Properties : " + props);

   				Enumeration<Object> keys = props.keys();
   				while (keys.hasMoreElements()) {
   					String key = (String) keys.nextElement();
   					boolean setProp=override || System.getProperty(key)==null || System.getProperty(key).equals("");
   					if (setProp) System.setProperty(key, props.getProperty(key));
   				}

   			} catch (Exception e) {
                   log.error("Error!", e);
   			}
   		}
   	}

    public static class RangeParameters {
        public String segmentId;
        public int start;
        public int end;
    }

    public RangeParameters getRangeParameters(HttpServletRequest request) throws Exception {
        RangeParameters params = new RangeParameters();

        String parameter = request.getParameter(ServletParameterType.RANGE);

        if (parameter == null) {
            throw new Exception("No parameter found, " + getParameterHelp());
        }

        log.info("locating " + parameter + " in " + this.sourceName);

        String parts1[] = parameter.split(":");

        if (parts1.length != 2) {
            throw new Exception("Illegal value in " + parameter + ", "+ getParameterHelp());
        }

        params.segmentId = parts1[0];
        String parts2[] = parts1[1].split("-");

        if (parts2.length != 2) {
            throw new Exception("Illegal value in " + parameter + ", " + getParameterHelp());
        }

        params.start = Integer.parseInt(parts2[0]);
        params.end   = Integer.parseInt(parts2[1]);

        return params;
    }

    public String getModeParameter(HttpServletRequest request) throws Exception {
        RangeParameters params = new RangeParameters();

        String parameter = request.getParameter(ServletParameterType.MODE);

        if (parameter == null) {
            throw new Exception("No 'm' parameter found, " + getParameterHelp());
        }

        if (!parameter.equalsIgnoreCase(ServletParameterType.EQTL) &&
                !parameter.equalsIgnoreCase(ServletParameterType.QUANT)) {

            throw new Exception("Illegal value in " + parameter + ", " + getParameterHelp());
        }

        return parameter;
    }

    public Properties getServletProperties() {
        return (Properties) getServletContext().getAttribute(ServletDataType.PROPERTIES);
    }

    public void initProperties() throws ServletException {
        Properties prop = new Properties();
       	try {
            //load a properties file
       		prop.load(ExonQuantRangeServlet.class.getResourceAsStream("/config.properties"));
            getServletContext().setAttribute(ServletDataType.PROPERTIES, prop);

       	} catch (IOException ex) {
           ex.printStackTrace();
        }
    }

    public HashMap<String, Object> getSourceMap() throws Exception {
        Object o = getServletContext().getAttribute(ServletDataType.SOURCEMAP);

        if (o == null) {
            HashMap<String, Object> map = new HashMap<String, Object>();

            getServletContext().setAttribute(ServletDataType.SOURCEMAP, map);

            o = map;
        }

        return (HashMap<String, Object>)o;
    }

    public Object getDataSource(String sourceName) throws Exception {
        return getSourceMap().get(sourceName);
    }

    public void saveDataSource(String sourceName, Object reader) throws Exception {
        getSourceMap().put(sourceName, reader);
    }

    public void init(ServletConfig sConfig) throws ServletException {
        super.init(sConfig);
    }

}
