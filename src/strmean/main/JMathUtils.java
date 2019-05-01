package strmean.main;

/**
 *
 * @author Docente
 */
public class JMathUtils {
    
    public static float min(float a, float b, float c) {
        if (a < b && a < c) {
            return a;
        } else if (b < c) {
            return b;
        } else {
            return c;
        }
    }

    public static float fmin(float a, float b, float c) {
        if (a < b && a < c) {
            return a;
        } else if (b < c) {
            return b;
        } else {
            return c;
        }
    }
}
