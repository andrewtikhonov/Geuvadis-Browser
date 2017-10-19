package services.util;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 28/11/2012
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    public static int oneK = 1024;
    public static int oneM = oneK * oneK;

    public static boolean isWithin(int pos, int start, int stop) {
        return (pos >= start && pos <= stop);
    }

    public static String humanizeSize(int size) {
        return (size > oneM ? (size / oneM + "M") : (size / oneK + "K"));
    }


}
