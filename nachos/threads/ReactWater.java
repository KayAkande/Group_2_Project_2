
package nachos.machine;
import nachos.machine.*;
import nachos.threads.*;
//import nachos.ag.*;


public class ReactWater {
    private Lock lock;
    private Condition2 hReady;
    private Condition2 oReady;
    private int hCount;
    private int oCount;

    public ReactWater() {
        lock = new Lock();
        hReady = new Condition2(lock);
        oReady = new Condition2(lock);
        hCount = 0;
        oCount = 0;
    }

    public void hReady() {
        lock.acquire();
        hCount++;
        while (hCount < 2 || oCount < 1) {
            hReady.sleep();
        }
        makeWater();
        hCount -= 2;
        oCount--;
        hReady.wake();
        hReady.wake();
        oReady.wake();
        lock.release();
    }

    public void oReady() {
        lock.acquire();
        oCount++;
        while (hCount < 2 || oCount < 1) {
            oReady.sleep();
        }
        makeWater();
        hCount -= 2;
        oCount--;
        hReady.wake();
        hReady.wake();
        oReady.wake();
        lock.release();
    }

    private void makeWater() {
        System.out.println("Water has been made!!");
    }
}
