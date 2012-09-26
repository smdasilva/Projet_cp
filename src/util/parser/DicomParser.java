package util.parser;

import java.util.List;

public class DicomParser implements Parser {

    private String extension;
    
    public DicomParser() {
	extension = "dcm";
    }
    
    @Override
    public String getExtension() {
	return extension;
    }

    @Override
    public void loadFile(String filename, Examen exam) {
	// TODO Auto-generated method stub
	
    }

    /** Not supported */
    @Override
    public boolean saveFile(String filename, Examen exam, List<Boolean> options) {
	return false;
    }

}
