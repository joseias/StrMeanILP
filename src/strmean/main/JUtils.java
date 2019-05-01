/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package strmean.main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 *
 * @author jabreu
 */
public final class JUtils {

    public static void initLogger() {
        try {
            Handler fh = new FileHandler("./"+JConstants.LOG_FILE_NAME);
            fh.setFormatter(new SimpleFormatter());
            Logger.getLogger("").addHandler(fh);
           
            
        } catch (IOException | SecurityException ex) {
            Logger.getLogger(JUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
