/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

/**
 *
 * @author Docente
 */
public class CallbackData {
    private double time = 0;
    private double obj = 0;
    private double[] sol = null;
    
    public CallbackData(double t, double o, double[] s){
        time = t;
        obj = o;
        sol = s;
    }

    /**
     * @return the time
     */
    public double getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * @return the obj
     */
    public double getObj() {
        return obj;
    }

    /**
     * @param obj the obj to set
     */
    public void setObj(double obj) {
        this.obj = obj;
    }

    /**
     * @return the sol
     */
    public double[] getSol() {
        return sol;
    }

    /**
     * @param sol the sol to set
     */
    public void setSol(double[] sol) {
        this.sol = sol;
    }
}
