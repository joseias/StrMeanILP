package distances;

import java.util.Properties;
import strmean.main.JConstants;
import strmean.main.JMathUtils;

public class EDLevenshtein {

    public SymbolDif _sd;

    public EDLevenshtein(Properties p) {
        this._sd = (SymbolDif) p.get(JConstants.SYMBOL_DIF);
    }

    public float dEdition(int[] x, int[] y) {

        float[][] al = new float[x.length + 1][y.length + 1];
        float a, b, c, wc, wa, wb;

        al[0][0] = 0;
        for (int i = 1; i <= x.length; i++) {
            al[i][0] = al[i - 1][0] + _sd.del(x[i - 1]);            //Borrado
        }
        for (int j = 1; j <= y.length; j++) {
            al[0][j] = al[0][j - 1] + _sd.ins(y[j - 1]);            //Inserción
        }
        for (int i = 1; i <= x.length; i++) {
            for (int j = 1; j <= y.length; j++) {
                a = al[i - 1][j] + _sd.del(x[i - 1]);               //Borrado
                b = al[i][j - 1] + _sd.ins(y[j - 1]);               //Inserción
                c = al[i - 1][j - 1] + _sd.sus(x[i - 1], y[j - 1]); //Sustitucion
                al[i][j] = JMathUtils.fmin(a, b, c);
            }
        }

        return al[x.length][y.length];
    }
}
