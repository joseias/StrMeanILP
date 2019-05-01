package strmean.data;

import distances.SymbolDif;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Docente
 */
public interface ISymbolDifParser {
        
        public SymbolDif fromFile(String path) throws FileNotFoundException, IOException ;
    
}
