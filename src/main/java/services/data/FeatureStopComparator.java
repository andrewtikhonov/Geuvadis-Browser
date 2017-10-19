package services.data;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 14/11/2012
 * Time: 13:10
 * To change this template use File | Settings | File Templates.
 */
public class FeatureStopComparator implements Comparator {
    public int compare(Object o1, Object o2) {

        int s1 = ((DataFeature) o1).getEnd();
        int s2 = ((DataFeature) o2).getEnd();

        if (s1 > s2)
            return 1;
        else if (s1 < s2)
            return -1;
        else
            return 0;
    }
}
