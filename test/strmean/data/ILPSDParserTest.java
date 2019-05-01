/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package strmean.data;

import distances.SymbolDif;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Docente
 */
public class ILPSDParserTest {
    
    public ILPSDParserTest() {
    }

    /**
     * Test of fromFile method, of class ILPSDParser.
     * @throws java.lang.Exception
     */
    @Test
    public void testFromFile() throws Exception {
        System.out.println("fromFile");
        String path = "test.in";
        ILPSDParser instance = new ILPSDParser();
        SymbolDif sd = instance.fromFile(path);
        
        float[] expResult = {6.0f, 7.0f, 8.0f}; 
        float[] result = sd.cSW[2];
        
        assertArrayEquals(expResult, result, 0);
        // TODO review the generated test code and remove the default call to fail.
    }
    
}
