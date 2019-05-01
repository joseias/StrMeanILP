/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package optimization;

import java.io.PrintStream;

/**
 *
 * @author Docente
 */
public class ILP_Triangular {
        public static void generaLP(float[][] matrizDistancias, String nombreSalida) throws Exception{
        PrintStream ps = new PrintStream(nombreSalida + ".lp");
        ps.println("Minimize");
        int tamano = matrizDistancias.length;
        String line = "  ";
        for (int i = 0; i < tamano-1; i++) {
            line += "DM" + i + " + ";
        }
        line += "DM" + (tamano-1);
        ps.println(line);
        ps.println("Subject To");
        for (int i = 0; i < tamano; i++) {
            for (int j = i+1; j < tamano; j++) {
                ps.println("  Distancia" + i + "_" + j + ": D" + i + "_" + j + " = " + matrizDistancias[i][j]  );
            }
        }
        for (int i = 0; i < tamano; i++) {
            for (int j = i+1; j < tamano ; j++) {
                ps.println("  Distancia1M" + i + "_" + j + ": D" + i + "_" + j + " - DM" + i + " - DM" + j + " <= 0" );
                ps.println("  Distancia2M" + i + "_" + j + ": DM" + j + " - DM" + i + " - D" + i + "_" + j + " <= 0" );
                ps.println("  Distancia3M" + i + "_" + j + ": DM" + i + " - DM" + j + " - D" + i + "_" + j + " <= 0" );
            }
        }
        ps.println("Bounds");
        ps.println("Integers");
        line = "  ";
        for (int i = 0; i < tamano-1; i++) {
            line += "DM" + i + " ";
        }
        line += "DM" + (tamano-1);
        ps.println(line);
        ps.println("End");
        ps.close();
    }
    
}
