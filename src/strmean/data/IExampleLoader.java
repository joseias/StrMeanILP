package strmean.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IExampleLoader {

    /**
     *
     * @param exstr
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public abstract List<Example> loadExamples(String exstr) throws FileNotFoundException, IOException ;

}
