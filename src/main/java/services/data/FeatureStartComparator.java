package services.data;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 14/11/2012
 * Time: 13:09
 * To change this template use File | Settings | File Templates.
 */
public class FeatureStartComparator implements Comparator {
    public int compare(Object o1, Object o2) {

        int s1 = ((DataFeature) o1).getStart();
        int s2 = ((DataFeature) o2).getStart();

        if (s1 > s2)
            return 1;
        else if (s1 < s2)
            return -1;
        else
            return 0;
    }
}
