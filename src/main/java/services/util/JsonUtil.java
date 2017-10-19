package services.util;

import javax.servlet.ServletOutputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 05/12/2012
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
public class JsonUtil {

    public static interface JsonObjectWriter {
        public void writeItem(Object o, ServletOutputStream output) throws Exception;
    }

    public static String toJson(String key, String value) {
        return '"' + key + '"'+ ':' + (value == null ? "null" : ( '"' + value + '"'));
    }

    public static void writeJsonList(List list, JsonObjectWriter writer, ServletOutputStream output) throws Exception {

        output.print("[");

        boolean first = true;

        for(Object o : list) {
            if (first) {
                first = !first;
            } else {
                output.print(",");
            }

            output.print("{");

            writer.writeItem(o, output);

            output.print("}");
        }

        output.print("]");
    }

}
