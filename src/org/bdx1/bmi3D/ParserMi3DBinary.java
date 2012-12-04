package org.bdx1.bmi3D;

import org.bdx1.diams.model.Examen;

public interface ParserMi3DBinary {
	
	public boolean save(String filename, Examen examen);
	
	public void load(String filename, Examen examen);

}
