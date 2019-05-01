package optimization;

import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBException;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import strmean.main.JConstants;

public class Callback extends GRBCallback {

    private GRBVar[] vars;
    private Properties p;
    private PrintStream ps;
    CallbackWorker cbw;
    BlockingQueue<CallbackData> queue;
    
    public Callback(GRBVar[] avars, Properties ap) {
        this.vars = avars;
        this.p = ap;
        
        try {
            String psp = (String)ap.get(JConstants.GRB_WRITE_PARCIAL);
            ps = new PrintStream(psp);
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").severe(sw.toString());
        }
        
        queue = new SynchronousQueue<>();
        cbw = new CallbackWorker(queue, ps);
        cbw.start();
    }

    @Override
    protected void callback() {
        try {
            if (where == GRB.CB_MIPSOL) {
                double time = this.getDoubleInfo(GRB.CB_RUNTIME);
                double obj = this.getDoubleInfo(GRB.CB_MIPSOL_OBJ);
                double[] sol = this.getSolution(vars);
                
                queue.put(new CallbackData(time, obj, sol));
                
    
            }
        } catch (GRBException | InterruptedException gbe) {
            StringWriter sw = new StringWriter();
            gbe.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").severe(sw.toString());
        }
    }
    
    public void dispose(){
        this.cbw.interrupt();
        ps.close();
    }

    public static Callback addCallback(GRBModel model, Properties p) {
        try {
            int m = (Integer) p.get(ILP_StrMean.M);

            GRBVar[] vars = new GRBVar[m + 1];
            vars[0] = model.getVarByName(ILP_StrMean.L);

            for (int i = 1; i <= m; i++) {
                vars[i] = model.getVarByName("t_" + i);
            }

            Callback cb = new Callback(vars, p);
            model.setCallback(cb);
            return cb;

        } catch (GRBException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));

            Logger.getLogger("").log(Level.SEVERE, "Error writting the model: {0}", sw.toString());
        }
        return null;
    }

}
