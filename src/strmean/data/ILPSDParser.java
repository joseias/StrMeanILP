package strmean.data;

import distances.SymbolDif;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Docente
 */
public class ILPSDParser implements ISymbolDifParser {

    @Override
    public SymbolDif fromFile(String path) throws FileNotFoundException, IOException  {
        SymbolDif sd = new SymbolDif();

        Scanner s = new Scanner(new File(path));

        s.nextInt();
        int tsym = s.nextInt() + 1;

        float[][] w = new float[tsym][tsym];

        for (int c = 0; c < tsym; ++c) {
            for (int f = 0; f < tsym; ++f) {
                w[c][f] = s.nextFloat();
            }
        }

        sd.ALPHABET_SIZE = tsym - 1;
        sd.cSW = w;
        
        s.close();
        return sd;
    }

    
    public static void main(String[] args) throws Exception{
        String path = "bench1_6_2_6.txt";
        ILPSDParser instance = new ILPSDParser();
        SymbolDif sd = instance.fromFile(path);
        
        float[] expResult = {0.0f, 1.0f, 1.0f}; 
        float[] result = sd.cSW[0];
        
        int k = 0;
    }
}
