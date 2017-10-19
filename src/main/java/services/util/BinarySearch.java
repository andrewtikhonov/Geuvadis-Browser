package services.util;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 28/11/2012
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
public class BinarySearch {

    public static int locateBinaryIndex(List list, int coord, BinaryCoordGetter getter) {
        //
        // 1000 x locateFeatureIndex =~ 5mills
        //
        //

        //log.info("--- locateFeatureIndex --- coordinate=" + coordinate + " mode=" + mode);

        int size = list.size();
        int delta = size / 2;
        int index = delta;

        for (;delta > 1;) {
            int fcoord = getter.getCoord(index);

            //log.info("index = " + index +" delta = " + delta + " fcoord = " + fcoord);

            if (coord < fcoord) {
                delta = delta / 2;
                index -= delta;
                continue;
            }

            if (coord >= fcoord) {
                delta = delta / 2;
                index += delta;
                continue;
            }
        }

        //DataFeature f = sortedFeatures.get(index);
        //log.info("located mode=" + mode + " coordinate=" + coordinate + " index="+ index);
        //log.info("located feature=" + f.getFeaureID() + " start=" + f.getStart()+ " end=" + f.getEnd());

        return index;
    }

}
