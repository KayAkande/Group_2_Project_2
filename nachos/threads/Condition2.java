package nachos.threads;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupts for
 * synchronization.
 */
public class Condition2 {
    private Lock conditionLock;
    private ThreadQueue waitQueue;

    /**
     * Allocate a new condition variable.
     *
     * @param   conditionLock   the lock associated with this condition variable
     */
    public Condition2(Lock conditionLock) {
        this.conditionLock = conditionLock;
        this.waitQueue = ThreadedKernel.scheduler.newThreadQueue(false);
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        boolean intStatus = Machine.interrupt().disable();
        conditionLock.release();

        waitQueue.waitForAccess(KThread.currentThread());
        KThread.sleep();

        conditionLock.acquire();
        Machine.interrupt().restore(intStatus);
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        boolean intStatus = Machine.interrupt().disable();

        KThread thread = waitQueue.nextThread();
        if (thread != null) {
            thread.ready();
        }

        Machine.interrupt().restore(intStatus);
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
        Lib.assertTrue(conditionLock.isHeldByCurrentThread());

        boolean intStatus = Machine.interrupt().disable();

        KThread thread = waitQueue.nextThread();
        while (thread != null) {
            thread.ready();
            thread = waitQueue.nextThread();
        }

        Machine.interrupt().restore(intStatus);
    }
}