package optimization;

import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * http://www.drdobbs.com/parallel/java-concurrency-queue-processing-part-2/232900063
 * @author Docente
 */
public class CallbackWorker extends Thread {

    private final BlockingQueue<CallbackData> queue;
    private final PrintStream ps;
    
    public CallbackWorker(BlockingQueue<CallbackData> queue, PrintStream ps) {
        this.queue = queue;
        this.ps = ps;
    }

    @Override
    public void run() {
        try {
            while (true) {
                CallbackData cd = queue.take();
                doJob(cd);
            }
        } catch (InterruptedException ie) {
            Logger.getLogger("").fine("CallbackWorker finished OK...");
        }
    }

    private void doJob(CallbackData cd) {
    
        ps.print("Obj:" + cd.getObj() + "-time:" + cd.getTime() + "-sol:");
        int L = (int) cd.getSol()[0];

        for (int i = 1; i <= L; i++) {
            ps.print((int) cd.getSol()[i] + " ");
        }
        ps.print("\n");
    }
}
