package util.parser;

import java.util.List;

public class Mi3DBinaryParser implements Parser {

    private String extension;
    
    public Mi3DBinaryParser() {
	extension = "bmi3d";
    }
    
    @Override
    public String getExtension() {
	return extension;
    }

    @Override
    public void loadFile(String filename, Examen exam) {
	// TODO Auto-generated method stub
	
    }

    @Override
    public boolean saveFile(String filename, Examen exam, List<Boolean> options) {
	// TODO Auto-generated method stub
	return false;
    }

}
