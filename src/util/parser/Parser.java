package util.parser;

import java.util.List;

public interface Parser {

    public String getExtension();
    
    public void loadFile(String filename, Examen exam);
    public boolean saveFile(String filename, Examen exam, List<Boolean> options);
    
}
