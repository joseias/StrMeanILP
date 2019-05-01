package strmean.data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ILPExampleLoader implements IExampleLoader {

    /**
     *
     * @param path
     * @return
     * @throws java.io.FileNotFoundException
     */
    @Override
    public List<Example> loadExamples(String path) throws FileNotFoundException, IOException  {
        
        
        String sep = " ";
        LineNumberReader lnr = new LineNumberReader(new FileReader(path));
        String l = lnr.readLine();
        String[] tokens = l.split(sep);

        int tseq = Integer.parseInt(tokens[0]);
        int tsym = Integer.parseInt(tokens[1]);

        int cl = 0;
        
        while(cl <= tsym ){
            lnr.readLine();
            ++cl;
        }

        List<Example> db = new ArrayList(tseq);
        
        int id = -1;
        for (int e = 0; e < tseq; ++e) {
            l = lnr.readLine();
            tokens = l.split(sep);
            int[] seq = Stream.of(tokens).mapToInt(s->{return Integer.parseInt(s);}).toArray();
            
            db.add(new Example(++id, seq));
        }
        return db;
        
    }
}
