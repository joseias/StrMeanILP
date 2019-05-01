/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package strmean.data;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Docente
 */
public class ILPExampleLoaderTest {
    
    public ILPExampleLoaderTest() {
    }

    /**
     * Test of loadExamples method, of class ILPExampleLoader.
     * @throws java.lang.Exception
     */
    @Test
    public void testLoadExamples() throws Exception {
        System.out.println("loadExamples");
        String path = "test.in";
        ILPExampleLoader instance = new ILPExampleLoader();

        List<Example> db = instance.loadExamples(path);
        
        int[] expResult = {1, 1, 1, 1, 2, 1};
        int[] result = db.get(5).sequence;
        
        assertArrayEquals(expResult, result);
    }
    
}
