package nachos.threads;

import nachos.machine.*;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
        Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
        });
    }
    
    // A queue thread data structure.
    private PriorityQueue<SleepyThread> sleepingQueue = new PriorityQueue<>(new SleepyThread.Comp());

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {
        System.out.println("Alarm class: starting timerInterrupt() method.");
        while (!sleepingQueue.isEmpty()) {
            System.out.println("Alarm class: timerInterrupt() while-loop iteration.");
            SleepyThread sleepyThread = sleepingQueue.peek();
            if (sleepyThread.priority <= Machine.timer().getTime()) {
                sleepingQueue.poll();
                sleepyThread.thread.ready();
            } else
                break;
        }
        Machine.interrupt().restore(Machine.interrupt().disable());
        KThread.currentThread().yield();
    }
    
    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {
	// for now, cheat just to get something working (busy waiting is bad)
        System.out.println("Alarm class: starting waitUntil(long x) method.");
        KThread curr = KThread.currentThread();
	long wakeTime = Machine.timer().getTime() + x;
        SleepyThread sleepyThread = new SleepyThread(curr, wakeTime);
        sleepingQueue.add(sleepyThread);
        curr.sleep();
        
        Machine.interrupt().restore(Machine.interrupt().disable());
        timerInterrupt();
    }
    
}

//New class to be implemented according to the design and the clues given.
class SleepyThread {
    
    public KThread thread;
    public long priority;
    
    public SleepyThread(KThread thread, long time) {
        this.thread = thread;
        this.priority = time;
    }
    
    public static class Comp implements Comparator<SleepyThread> {
        public int compare(SleepyThread a, SleepyThread b) {
            if (a.priority > b.priority)
                return 1;
            else if (a.priority < b.priority)
                return -1;
            return 0;
        }
    }
}