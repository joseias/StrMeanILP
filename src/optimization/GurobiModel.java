package optimization;

import distances.SymbolDif;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import strmean.data.Example;
import strmean.main.JConstants;

public class GurobiModel {

    public GRBModel getGRBModel(List<Example> db, Properties p) {
        GRBModel model = null;
        SymbolDif sd = (SymbolDif) p.get(JConstants.SYMBOL_DIF);
        try {
            //<editor-fold defaultstate="collapsed" desc="Injecting dependencies">
            String grbLogFile = p.getProperty(JConstants.GRB_LOG_FILE_NAME);
            //<editor-fold defaultstate="collapsed" desc="Injecting dependencies">

            // Get the max lenght
            Comparator<Example> cmp = (Example e1, Example e2) -> Integer.compare(e1.sequence.length, e2.sequence.length);

            int m = db.stream().max(cmp).get().sequence.length;
            p.put(ILP_StrMean.M, m * 2);
            p.put(ILP_StrMean.L, m);
            p.put(ILP_StrMean.A, sd.ALPHABET_SIZE);

            GRBEnv env = new GRBEnv(grbLogFile);
            model = new GRBModel(env);

            addVariables(model, db, p);
            addConstraintA1(model, db, p);
            addConstrainsA2(model, db, p);
            addConstrainsA3(model, db, p);
            addConstrainsA4(model, db, p);
            addConstrainsA5(model, db, p);
            addConstrainsA6(model, db, p);
            addConstrainsA7(model, db, p);
            addConstrainsA8(model, db, p);
            addConstrainsA9(model, db, p);
            addConstrainsB(model, db, p);
            addConstrainsC1(model, db, p);
            addConstrainsC2(model, db, p);
            addConstrainsD1(model, db, p);
            addConstrainsD2(model, db, p);
            addObjective(model, db, p);

            return model;
        } catch (GRBException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").log(Level.SEVERE, "Error code: {0}. {1}", new Object[]{e.getErrorCode(), sw.toString()});
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Logger.getLogger("").log(Level.SEVERE, sw.toString());
        }
        return model;
    }

    public void addVariables(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        String varName;
        String sub;
        int m = (Integer) p.get(ILP_StrMean.M);
        int a = (Integer) p.get(ILP_StrMean.A);

        model.addVar(0, m, 0.0, GRB.INTEGER, ILP_StrMean.L);

        for (int j = 1; j <= m; j++) {
            String tj = "t_" + j;
            model.addVar(1, a, 0.0, GRB.INTEGER, tj);
        }

        for (Example ek : db) {
            for (int i = 0; i <= ek.sequence.length; i++) {
                for (int j = 0; j <= m; j++) {
                    if ((!(i == 0 && j == 0))) {
                        sub = ek.ID + "_" + i + "_" + j;

                        if (isValidEdge('x', i, j)) {
                            varName = "x_" + sub;
                            model.addVar(0.0, 1.0, 0.0, GRB.BINARY, varName);
                        }

                        if (isValidEdge('y', i, j)) {
                            varName = "y_" + sub;
                            model.addVar(0.0, 1.0, 0.0, GRB.BINARY, varName);
                        }

                        if (isValidEdge('z', i, j)) {
                            varName = "z_" + sub;
                            model.addVar(0.0, 1.0, 0.0, GRB.BINARY, varName);
                        }

                        if (isValidEdge('g', i, j)) {
                            varName = "g_" + sub;
                            model.addVar(0.0, 1.0, 0.0, GRB.BINARY, varName);
                        }

                        if (isValidEdge('h', i, j)) {
                            varName = "h_" + sub;
                            model.addVar(0.0, 1.0, 0.0, GRB.BINARY, varName);
                        }
                    }
                }
            }
        }
    }

    public void addConstraintA1(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        for (Example ek : db) {
            String xk10 = "x_" + ek.ID + "_1_0";
            String yk01 = "y_" + ek.ID + "_0_1";
            String zk11 = "z_" + ek.ID + "_1_1";
            String cName = "a1_" + ek.ID;

            GRBVar xk10v = model.getVarByName(xk10);
            GRBVar yk01v = model.getVarByName(yk01);
            GRBVar zk11v = model.getVarByName(zk11);

            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1.0, xk10v);
            expr.addTerm(1.0, yk01v);
            expr.addTerm(1.0, zk11v);

            model.addConstr(expr, GRB.EQUAL, 1.0, cName);
        }
    }

    public void addConstrainsA2(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        for (Example ek : db) {
            for (int i = 1; i < ek.sequence.length; i++) {
                String xki0 = "x_" + ek.ID + "_" + i + "_0";
                String xkip10 = "x_" + ek.ID + "_" + (i + 1) + "_0";
                String yki1 = "y_" + ek.ID + "_" + i + "_1";
                String zkip10 = "z_" + ek.ID + "_" + (i + 1) + "_1";
                String cName = "a2_" + ek.ID + "_" + i;

                GRBVar xki0v = model.getVarByName(xki0);
                GRBVar xkip10v = model.getVarByName(xkip10);
                GRBVar yki1v = model.getVarByName(yki1);
                GRBVar zkip10v = model.getVarByName(zkip10);

                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(-1.0, xki0v);
                expr.addTerm(1.0, xkip10v);
                expr.addTerm(1.0, yki1v);
                expr.addTerm(1.0, zkip10v);

                model.addConstr(expr, GRB.EQUAL, 0.0, cName);

            }
        }
    }

    public void addConstrainsA3(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        for (Example ek : db) {
            String xknk0 = "x_" + ek.ID + "_" + ek.sequence.length + "_0";
            String yknk1 = "y_" + ek.ID + "_" + ek.sequence.length + "_1";
            String cName = "a3_" + ek.ID;

            GRBVar xknk0v = model.getVarByName(xknk0);
            GRBVar yknk1v = model.getVarByName(yknk1);

            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1.0, xknk0v);
            expr.addTerm(-1.0, yknk1v);

            model.addConstr(expr, GRB.EQUAL, 0.0, cName);
        }
    }

    public void addConstrainsA4(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        int m = (Integer) p.get(ILP_StrMean.M);

        for (Example ek : db) {
            for (int j = 1; j < m; j++) {
                String yk0j = "y_" + ek.ID + "_0_" + j;
                String xk1j = "x_" + ek.ID + "_1_" + j;
                String yk0jp1 = "y_" + ek.ID + "_0_" + (j + 1);
                String zk1jp1 = "z_" + ek.ID + "_1_" + (j + 1);
                String cName = "a4_" + ek.ID + "_" + j;

                GRBVar yk0jv = model.getVarByName(yk0j);
                GRBVar xk1jv = model.getVarByName(xk1j);
                GRBVar yk0jp1v = model.getVarByName(yk0jp1);
                GRBVar zk1jp1v = model.getVarByName(zk1jp1);

                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1.0, yk0jv);
                expr.addTerm(-1.0, xk1jv);
                expr.addTerm(-1.0, yk0jp1v);
                expr.addTerm(-1.0, zk1jp1v);

                model.addConstr(expr, GRB.EQUAL, 0.0, cName);
            }
        }
    }

    public void addConstrainsA5(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();
        int m = (Integer) p.get(ILP_StrMean.M);

        for (Example ek : db) {
            String yk0m = "y_" + ek.ID + "_0_" + m;
            String xk1m = "x_" + ek.ID + "_1_" + m;
            String cName = "a5_" + ek.ID;

            GRBVar yk0mv = model.getVarByName(yk0m);
            GRBVar xk1mv = model.getVarByName(xk1m);

            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1.0, yk0mv);
            expr.addTerm(-1.0, xk1mv);

            model.addConstr(expr, GRB.EQUAL, 0.0, cName);
        }
    }

    public void addConstrainsA6(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        int m = (Integer) p.get(ILP_StrMean.M);

        for (Example ek : db) {
            for (int i = 1; i < ek.sequence.length; i++) {
                for (int j = 1; j < m; j++) {
                    String xkij = "x_" + ek.ID + "_" + i + "_" + j;
                    String ykij = "y_" + ek.ID + "_" + i + "_" + j;
                    String zkij = "z_" + ek.ID + "_" + i + "_" + j;

                    String xkip1j = "x_" + ek.ID + "_" + (i + 1) + "_" + j;
                    String ykijp1 = "y_" + ek.ID + "_" + i + "_" + (j + 1);
                    String zkip1jp1 = "z_" + ek.ID + "_" + (i + 1) + "_" + (j + 1);

                    String cName = "a6_" + ek.ID + "_" + i + "_" + j;
                    GRBVar xkijv = model.getVarByName(xkij);
                    GRBVar ykijv = model.getVarByName(ykij);
                    GRBVar zkijv = model.getVarByName(zkij);

                    GRBVar xkip1jv = model.getVarByName(xkip1j);
                    GRBVar ykijp1v = model.getVarByName(ykijp1);
                    GRBVar zkip1jp1v = model.getVarByName(zkip1jp1);

                    GRBLinExpr expr = new GRBLinExpr();
                    expr.addTerm(1.0, xkijv);
                    expr.addTerm(1.0, ykijv);
                    expr.addTerm(1.0, zkijv);
                    expr.addTerm(-1.0, xkip1jv);
                    expr.addTerm(-1.0, ykijp1v);
                    expr.addTerm(-1.0, zkip1jp1v);

                    model.addConstr(expr, GRB.EQUAL, 0.0, cName);
                }
            }

        }
    }

    public void addConstrainsA7(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();
        int m = (Integer) p.get(ILP_StrMean.M);
        for (Example ek : db) {
            for (int j = 1; j < m; j++) {
                String xknkj = "x_" + ek.ID + "_" + ek.sequence.length + "_" + j;
                String yknkj = "y_" + ek.ID + "_" + ek.sequence.length + "_" + j;
                String zknkj = "z_" + ek.ID + "_" + ek.sequence.length + "_" + j;
                String yknkjp1 = "y_" + ek.ID + "_" + ek.sequence.length + "_" + (j + 1);
                String cName = "a7_" + ek.ID + "_" + j;

                GRBVar xknkjv = model.getVarByName(xknkj);
                GRBVar yknkjv = model.getVarByName(yknkj);
                GRBVar zknkjv = model.getVarByName(zknkj);
                GRBVar yknkjp1v = model.getVarByName(yknkjp1);

                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1.0, xknkjv);
                expr.addTerm(1.0, yknkjv);
                expr.addTerm(1.0, zknkjv);
                expr.addTerm(-1.0, yknkjp1v);

                model.addConstr(expr, GRB.EQUAL, 0.0, cName);
            }
        }
    }

    public void addConstrainsA8(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        int m = (Integer) p.get(ILP_StrMean.M);

        for (Example ek : db) {
            for (int i = 1; i < ek.sequence.length; i++) {
                String xkim = "x_" + ek.ID + "_" + i + "_" + m;
                String ykim = "y_" + ek.ID + "_" + i + "_" + m;
                String zkim = "z_" + ek.ID + "_" + i + "_" + m;
                String xkip1m = "x_" + ek.ID + "_" + (i + 1) + "_" + m;
                String cName = "a8_" + ek.ID + "_" + i;

                GRBVar xkimv = model.getVarByName(xkim);
                GRBVar ykimv = model.getVarByName(ykim);
                GRBVar zkimv = model.getVarByName(zkim);
                GRBVar xkip1mv = model.getVarByName(xkip1m);

                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(1.0, xkimv);
                expr.addTerm(1.0, ykimv);
                expr.addTerm(1.0, zkimv);
                expr.addTerm(-1.0, xkip1mv);

                model.addConstr(expr, GRB.EQUAL, 0.0, cName);

            }
        }
    }

    public void addConstrainsA9(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        int m = (Integer) p.get(ILP_StrMean.M);

        for (Example ek : db) {
            String xnkm = "x_" + ek.ID + "_" + ek.sequence.length + "_" + m;
            String ynkm = "y_" + ek.ID + "_" + ek.sequence.length + "_" + m;
            String znkm = "z_" + ek.ID + "_" + ek.sequence.length + "_" + m;
            String cName = "a9_" + ek.ID;

            GRBVar xnkmv = model.getVarByName(xnkm);
            GRBVar ynkmv = model.getVarByName(ynkm);
            GRBVar znkmv = model.getVarByName(znkm);

            GRBLinExpr expr = new GRBLinExpr();
            expr.addTerm(1.0, xnkmv);
            expr.addTerm(1.0, ynkmv);
            expr.addTerm(1.0, znkmv);

            model.addConstr(expr, GRB.EQUAL, 1.0, cName);
        }
    }

    public void addConstrainsB(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        double m = ((Integer) p.get(ILP_StrMean.M)).doubleValue();
        GRBVar Lv = model.getVarByName(ILP_StrMean.L);

        for (Example ek : db) {
            for (int j = 1; j <= m; j++) {
                String yknkj = "y_" + ek.ID + "_" + ek.sequence.length + "_" + j;
                String cName = "b_" + ek.ID + "_" + j;

                GRBVar yknkjv = model.getVarByName(yknkj);

                GRBLinExpr expr = new GRBLinExpr();
                expr.addTerm(m, yknkjv);
                expr.addTerm(1.0, Lv);

                model.addConstr(expr, GRB.GREATER_EQUAL, j, cName);
            }
        }
    }

    public void addConstrainsC1(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        int m = (Integer) p.get(ILP_StrMean.M);
        int a = (Integer) p.get(ILP_StrMean.A);

        for (Example ek : db) {
            for (int i = 1; i <= ek.sequence.length; i++) {
                for (int j = 1; j <= m; j++) {
                    String tj = "t_" + j;
                    String gkij = "g_" + ek.ID + "_" + i + "_" + j;
                    String cName = "c1_" + ek.ID + "_" + i + "_" + j;

                    GRBVar tjv = model.getVarByName(tj);
                    GRBVar gkijv = model.getVarByName(gkij);

                    GRBLinExpr expr = new GRBLinExpr();
                    expr.addTerm(1.0, tjv);
                    expr.addTerm(a, gkijv);

                    model.addConstr(expr, GRB.GREATER_EQUAL, ek.sequence[i - 1], cName);
                }
            }
        }
    }

    public void addConstrainsC2(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        int m = (Integer) p.get(ILP_StrMean.M);
        int a = (Integer) p.get(ILP_StrMean.A);

        for (Example ek : db) {
            for (int i = 1; i <= ek.sequence.length; i++) {
                for (int j = 1; j <= m; j++) {
                    String tj = "t_" + j;
                    String gkij = "g_" + ek.ID + "_" + i + "_" + j;
                    String cName = "c2_" + ek.ID + "_" + i + "_" + j;

                    GRBVar tjv = model.getVarByName(tj);
                    GRBVar gkijv = model.getVarByName(gkij);

                    GRBLinExpr expr = new GRBLinExpr();

                    expr.addTerm(1.0, tjv);
                    expr.addTerm(-a, gkijv);

                    model.addConstr(expr, GRB.LESS_EQUAL, ek.sequence[i - 1], cName);
                }
            }

        }
    }

    public void addConstrainsD1(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        int m = (Integer) p.get(ILP_StrMean.M);

        for (Example ek : db) {
            for (int i = 1; i <= ek.sequence.length; i++) {
                for (int j = 1; j <= m; j++) {
                    String hkij = "h_" + ek.ID + "_" + i + "_" + j;
                    String zkij = "z_" + ek.ID + "_" + i + "_" + j;
                    String gkij = "g_" + ek.ID + "_" + i + "_" + j;
                    String cName = "d1_" + ek.ID + "_" + i + "_" + j;

                    GRBVar hkijv = model.getVarByName(hkij);
                    GRBVar zkijv = model.getVarByName(zkij);
                    GRBVar gkijv = model.getVarByName(gkij);

                    GRBLinExpr expr = new GRBLinExpr();

                    expr.addTerm(1.0, hkijv);
                    expr.addTerm(-1.0, zkijv);
                    expr.addTerm(-1.0, gkijv);

                    model.addConstr(expr, GRB.GREATER_EQUAL, -1.0, cName);
                }
            }

        }
    }

    public void addConstrainsD2(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();

        int m = (Integer) p.get(ILP_StrMean.M);

        for (Example ek : db) {
            for (int i = 1; i <= ek.sequence.length; i++) {
                for (int j = 1; j <= m; j++) {
                    String hkij = "h_" + ek.ID + "_" + i + "_" + j;
                    String zkij = "z_" + ek.ID + "_" + i + "_" + j;
                    String gkij = "g_" + ek.ID + "_" + i + "_" + j;
                    String cName = "d2_" + ek.ID + "_" + i + "_" + j;

                    GRBVar hkijv = model.getVarByName(hkij);
                    GRBVar zkijv = model.getVarByName(zkij);
                    GRBVar gkijv = model.getVarByName(gkij);

                    GRBLinExpr expr = new GRBLinExpr();

                    expr.addTerm(2.0, hkijv);
                    expr.addTerm(-1.0, zkijv);
                    expr.addTerm(-1.0, gkijv);

                    model.addConstr(expr, GRB.LESS_EQUAL, 0.0, cName);
                }
            }

        }
    }

    public void addObjective(GRBModel model, List<Example> db, Properties p) throws GRBException {
        model.update();
        GRBLinExpr expr = new GRBLinExpr();
        addObjectiveDel(model, db, p, expr);
        addObjectiveIns(model, db, p, expr);
        addObjectiveSub(model, db, p, expr);
        addObjectiveL(model, db, p, expr);

        model.setObjective(expr, GRB.MINIMIZE);
    }

    public void addObjectiveDel(GRBModel model, List<Example> db, Properties p, GRBLinExpr expr) throws GRBException {
        SymbolDif sd = (SymbolDif) p.get(JConstants.SYMBOL_DIF);
        for (Example ek : db) {

            for (int i = 1; i <= ek.sequence.length; i++) {
                String xki0 = "x_" + ek.ID + "_" + i + "_0";
                GRBVar xki0v = model.getVarByName(xki0);
                expr.addTerm(sd.CDEL, xki0v);
            }
        }
    }

    private void addObjectiveIns(GRBModel model, List<Example> db, Properties p, GRBLinExpr expr) throws GRBException {
        SymbolDif sd = (SymbolDif) p.get(JConstants.SYMBOL_DIF);
        int m = (Integer) p.get(ILP_StrMean.M);

        for (Example ek : db) {

            for (int j = 1; j <= m; j++) {
                String yki0 = "y_" + ek.ID + "_0_" + j;
                GRBVar yki0v = model.getVarByName(yki0);
                expr.addTerm(sd.CINS, yki0v);
            }
        }
    }

    private void addObjectiveSub(GRBModel model, List<Example> db, Properties p, GRBLinExpr expr) throws GRBException {
        SymbolDif sd = (SymbolDif) p.get(JConstants.SYMBOL_DIF);
        int m = (Integer) p.get(ILP_StrMean.M);

        for (Example ek : db) {
            for (int i = 1; i <= ek.sequence.length; i++) {
                for (int j = 1; j <= m; j++) {
                    String xkij = "x_" + ek.ID + "_" + i + "_" + j;
                    String ykij = "y_" + ek.ID + "_" + i + "_" + j;
                    String hkij = "h_" + ek.ID + "_" + i + "_" + j;

                    GRBVar xkijv = model.getVarByName(xkij);
                    GRBVar ykijv = model.getVarByName(ykij);
                    GRBVar hkijv = model.getVarByName(hkij);

                    expr.addTerm(sd.CDEL, xkijv);
                    expr.addTerm(sd.CINS, ykijv);
                    expr.addTerm(sd.CSUB, hkijv);
                }
            }
        }
    }

    private void addObjectiveL(GRBModel model, List<Example> db, Properties p, GRBLinExpr expr) throws GRBException {
        SymbolDif sd = (SymbolDif) p.get(JConstants.SYMBOL_DIF);
        int m = (Integer) p.get(ILP_StrMean.M);
        GRBVar Lv = model.getVarByName(ILP_StrMean.L);
        int N = db.size();

        expr.addTerm(sd.CINS * N, Lv);
        expr.addConstant(-sd.CINS * N * m);
    }

    public static boolean isValidEdge(char et, int i, int j) {
        switch (et) {
            case 'x':
                return i != 0;
            case 'y':
                return j != 0;
            case 'z':
                return (i != 0 && j != 0);
            case 'g':
                return (i != 0 && j != 0);
            case 'h':
                return (i != 0 && j != 0);
            default:
                return false;
        }
    }
}
