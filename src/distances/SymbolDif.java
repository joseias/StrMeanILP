package distances;

import java.io.Serializable;


/**
 * Wraps the sustitution matrix
*/
public class SymbolDif implements Serializable
{

    public static double CINS = 1.0;
    public static double CDEL = 1.0;
    public static double CSUB = 1.0;
    public static int ALPHABET_SIZE = -1;
    
    public float[][] cSW;
    
    /**
     * Sustitution
     * 
     * @param a_symbol1
     * @param a_symbol2
     * @return
     */
    public float sus(int a_symbol1,int a_symbol2){   
        return cSW[a_symbol1][a_symbol2];
    }
    
    public float ins(int a_symbol){
        return cSW[0][a_symbol];
    }
    
    public float del(int a_symbol){
        return cSW[a_symbol][0];
    }

}
