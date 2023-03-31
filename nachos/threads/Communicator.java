package nachos.threads;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {
    private Lock lock;
    private Condition2 speakCondition;
    private Condition2 listenCondition;
    private int word;
    private boolean spoken;
    /**
     * Allocate a new communicator.
     */
    public Communicator() {
        lock = new Lock();
        speakCondition = new Condition2(lock);
        listenCondition = new Condition2(lock);
        word = 0;
        spoken = false;
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) {
        
                lock.acquire();
       
            while (spoken) {
                listenCondition.wake();
                speakCondition.sleep();
            }
            this.word = word;
            spoken = true;
            listenCondition.wake();

            lock.release();
        
        
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() {
      lock.acquire();
        int listenedWord = 0;
        
            while (!spoken) {
                speakCondition.wake();
                listenCondition.sleep();
            }
            listenedWord = word;
            spoken = false;
            speakCondition.wake();
       
            lock.release();
        return listenedWord;
    }
    
}
