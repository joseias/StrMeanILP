package optimization;

import distances.SymbolDif;
import gurobi.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import strmean.data.Example;
import strmean.data.IExampleLoader;
import strmean.data.ILPExampleLoader;
import strmean.data.ILPSDParser;
import strmean.data.ISymbolDifParser;
import strmean.main.JConstants;
import strmean.main.JUtils;

public class ILP_StrMean {

    public static final String M = "M";
    public static final String L = "L";
    public static final String A = "A";

    public static void main(String[] args) {
        try {
            JUtils.initLogger();
            //<editor-fold defaultstate="collapsed" desc="Injecting dependencies">
            Properties p = new Properties();
            //</editor-fold>

            CommandLine cmd;
            Callback cb = null;
            
            if ((cmd = getOptions(args)) != null) {

                String fp = cmd.getOptionValue(JConstants.GRB_INPUT_FILE);

                /* Load SymbolDif*/
                ISymbolDifParser sdp = new ILPSDParser();
                SymbolDif sd = sdp.fromFile(fp);
                p.put(JConstants.SYMBOL_DIF, sd);

                /* Load dataset*/
                IExampleLoader el = new ILPExampleLoader();
                List<Example> db = el.loadExamples(fp);

                GurobiModel gm = new GurobiModel();
                GRBModel model = gm.getGRBModel(db, p);

                GRBEnv env = new GRBEnv(JConstants.GRB_ENV_LOG);

                if (cmd.hasOption(JConstants.GRB_VERBOSE)) {
                    model.getEnv().set(GRB.IntParam.LogToConsole, 1);
                }
                else {
                    model.getEnv().set(GRB.IntParam.LogToConsole, 0);
                }

                if (cmd.hasOption(JConstants.GRB_WRITE_MODEL)) {
                    writeModel(model, cmd.getOptionValue(JConstants.GRB_WRITE_MODEL));
                }

                if (cmd.hasOption(JConstants.GRB_WRITE_PARCIAL)) {
                    String psp = cmd.getOptionValue(JConstants.GRB_WRITE_PARCIAL);
                    p.put(JConstants.GRB_WRITE_PARCIAL, psp);
                    cb = Callback.addCallback(model, p);
                }

                if (cmd.hasOption(JConstants.GRB_OPTIMIZE_MODEL)) {
                    model.optimize();

                    if (cmd.hasOption(JConstants.GRB_VERBOSE)) {
                        printModelSumary(model, p);
                    }
                }

                if (cmd.hasOption(JConstants.GRB_VERBOSE)) {
                    printVariables(model, db, p);
                }

                

                // Dispose of model and environment
                model.dispose();
                env.dispose();
                
                if (cmd.hasOption(JConstants.GRB_WRITE_PARCIAL) && cb!=null ) {
                    cb.dispose();
                }
            }
            else {
                Logger.getLogger("").severe("Abnormal termination, check command line arguments...");
            }
        } 
        catch(FileNotFoundException fnfe){
            StringWriter sw = new StringWriter();
            fnfe.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").log(Level.SEVERE, "Check input files: ", sw.toString());
        }
            
        catch(IOException ioe){
            StringWriter sw = new StringWriter();
            ioe.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").log(Level.SEVERE, "Check input files format: ", sw.toString());
        }
        catch (GRBException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").log(Level.SEVERE, sw.toString());
        }
    }

    public static void gurobiTest() {
        try {
            GRBEnv env = new GRBEnv("mip1.log");
            GRBModel model = new GRBModel(env);

            // Create variables
            GRBVar x = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x1");
            GRBVar y = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "y1");
            GRBVar z = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z1");

            // Set objective: maximize x + y + 2 z
            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(1.0, y);
            expr.addTerm(2.0, z);
            model.setObjective(expr, GRB.MAXIMIZE);

            // Add constraint: x + 2 y + 3 z <= 4
            expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(2.0, y);
            expr.addTerm(3.0, z);
            model.addConstr(expr, GRB.LESS_EQUAL, 4.0, "c0");

            // Add constraint: x + y >= 1
            expr = new GRBLinExpr();
            expr.addTerm(1.0, x);
            expr.addTerm(1.0, y);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c1");
            model.write("gurobi.lp");

            GRBVar tmp = model.getVarByName("x1");

            // Optimize model
            model.optimize();
            System.out.println(x.get(GRB.StringAttr.VarName)
                    + " " + x.get(GRB.DoubleAttr.X));
            System.out.println(y.get(GRB.StringAttr.VarName)
                    + " " + y.get(GRB.DoubleAttr.X));
            System.out.println(z.get(GRB.StringAttr.VarName)
                    + " " + z.get(GRB.DoubleAttr.X));
            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            // Dispose of model and environment

            model.dispose();
            env.dispose();
            
        } catch (GRBException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").log(Level.SEVERE, "Error code: {0}. {1}", new Object[]{e.getErrorCode(), sw.toString()});
        }
    }

    public static void printVariables(GRBModel model, List<Example> db, Properties p) throws GRBException {
        String sub;
        String varName;
        int m = (Integer) p.get(ILP_StrMean.M);

        List<String> vars = new ArrayList<>(model.getVars().length);

        Example ek = db.get(0);

        for (int i = 0; i <= ek.sequence.length; i++) {
            for (int j = 0; j <= m; j++) {
                if ((!(i == 0 && j == 0))) {
                    sub = ek.ID + "_" + i + "_" + j;

                    if (GurobiModel.isValidEdge('x', i, j)) {
                        varName = "x_" + sub;
                        if (model.getVarByName(varName).get(GRB.DoubleAttr.X) != 0.0) {
                            vars.add(varName + ": " + model.getVarByName(varName).get(GRB.DoubleAttr.X));
                        }

                    }

                    if (GurobiModel.isValidEdge('y', i, j)) {
                        varName = "y_" + sub;
                        if (model.getVarByName(varName).get(GRB.DoubleAttr.X) != 0.0) {
                            vars.add(varName + ": " + model.getVarByName(varName).get(GRB.DoubleAttr.X));
                        }
                    }

                    if (GurobiModel.isValidEdge('z', i, j)) {
                        varName = "z_" + sub;
                        if (model.getVarByName(varName).get(GRB.DoubleAttr.X) != 0.0) {
                            vars.add(varName + ": " + model.getVarByName(varName).get(GRB.DoubleAttr.X));
                        }
                    }

                    if (GurobiModel.isValidEdge('g', i, j)) {
                        varName = "g_" + sub;
                        if (model.getVarByName(varName).get(GRB.DoubleAttr.X) != 0.0) {
                            vars.add(varName + ": " + model.getVarByName(varName).get(GRB.DoubleAttr.X));
                        }
                    }

                    if (GurobiModel.isValidEdge('h', i, j)) {
                        varName = "h_" + sub;
                        if (model.getVarByName(varName).get(GRB.DoubleAttr.X) != 0.0) {
                            vars.add(varName + ": " + model.getVarByName(varName).get(GRB.DoubleAttr.X));
                        }
                    }
                }
            }
        }

        vars.sort((s1, s2) -> {
            return s1.compareTo(s2);
        });
        vars.forEach(s -> {
            System.out.println(s);
        });

    }

    public static void printModelSumary(GRBModel model, Properties p) {
        try {
            GRBVar l = model.getVarByName(ILP_StrMean.L);
            int ls = (int) l.get(GRB.DoubleAttr.X);

            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            for (int i = 1; i <= ls; i++) {
                System.out.print((int) model.getVarByName("t_" + i).get(GRB.DoubleAttr.X) + " ");
            }
            System.out.println();
        } catch (GRBException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").log(Level.SEVERE, "Error printing model summary: ", sw.toString());
        }
    }

    public static void writeModel(GRBModel model, String modelPath) {
        try {
            model.write(modelPath);
        } catch (GRBException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").log(Level.SEVERE, "Error writting the model: ", sw.toString());
        }
    }

    public static CommandLine getOptions(String[] args) {
        CommandLineParser parser = new DefaultParser();
        Options options = new Options();
        options.addOption(JConstants.GRB_WRITE_MODEL, true, "Prints the model in the specified file...");
        options.addOption(JConstants.GRB_INPUT_FILE, true, "Path to the input examples...");
        options.addOption(JConstants.GRB_PROPERTIES_FILE, true, "Path to the properties file...");
        options.addOption(JConstants.GRB_WRITE_PARCIAL, true, "Save partial solutions...");
        options.addOption(JConstants.GRB_OPTIMIZE_MODEL, false, "Optimize the model");
        options.addOption(JConstants.GRB_VERBOSE, false, "Output extra information...");

        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException ex) {

            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));    
            Logger.getLogger("").log(Level.SEVERE, "Error running the command {0} \n {1} : ", new Object[]{ex.getMessage(), sw.toString()});
        }
        return cmd;
    }

}
