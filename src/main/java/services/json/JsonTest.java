package services.json;

import com.google.gson.Gson;
import services.data.DataFeature;
import services.data.FeatureType;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 23/11/2012
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
public class JsonTest {

    public JsonTest(){
        //JSONObject jsobj = new JSONObject()

        Gson gson = new Gson();
        //String featureType, String feaureID, String displayName, int start, int end, double score

        DataFeature f = new DataFeature(FeatureType.EXON, "Feature1", "chr1", 10000, 20000, 0.05);

        ArrayList<DataFeature> list = new ArrayList<DataFeature>();

        list.add(f);
        list.add(f);
        list.add(f);
        list.add(f);

        String json = gson.toJson(list);

        //String json_simple = JSONValue.toJSONString(list);


        System.out.println("json=" + json);
        //System.out.println("json_simple=" + json_simple);
    }

    public static void main(String[] args) {
        new JsonTest();

    }
}
