package services.util;

/**
 * Created with IntelliJ IDEA.
 * User: andrew
 * Date: 05/12/2012
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class ProgressTracker {

    public int available = 0;
    public int loaded0 = 0;
    public int loaded1 = 0;

    public ProgressTracker(int available) {
        this.available = available;
        this.loaded1   = available / 10;
    }

    public boolean track(int loaded0) {
        this.loaded0 += loaded0;
        return (this.loaded0 > loaded1);
    }

    public String report() {
        loaded1 += available / 10;
        return ((long)loaded0 * 100 / available) + "%";
    }
}
