package org.bdx1.bmi3D;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.bdx1.diams.model.Examen;
import org.bdx1.diams.model.Mask;

import com.pixelmed.dicom.TagFromName;


public class ParserMi3DBinaryCommonFormat implements ParserMi3DBinary {

	private final float default_resolution = 1;

	private String extension;

	public ParserMi3DBinaryCommonFormat() {
		extension = "bmi";
	}

	public boolean save(String filename, Examen examen) {
		filename = filename.trim();
		filename += extension;
		
		boolean dataflag = (!examen.getStudyInfos().isEmpty());
		boolean volumeflag = (examen.getMask() == null); //il me faut le masque
		boolean skeletonflag = false; //le skeleton si vous l'avez
		boolean informationflag = (!examen.getPatientInfos().isEmpty());
		
		//Complèter ces 3 valeurs
		byte width = 1;
		byte heigth = 1;
		byte depth = 1;
		
		float resolutionX = default_resolution;
		float resolutionY = default_resolution;
		float resolutionZ = default_resolution;
		
		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			
			//Header
			buf.write(booleanTbyte(dataflag));
			buf.write(booleanTbyte(volumeflag));
			buf.write(booleanTbyte(skeletonflag));
			buf.write(booleanTbyte(informationflag));
			
			buf.write(width);
			buf.write(heigth);
			buf.write(depth);
			buf.write((byte) resolutionX);
			buf.write((byte) resolutionY);
			buf.write((byte) resolutionZ);
			
			if (volumeflag) {
				Mask mask = examen.getMask();
				byte[][] bmiMask = new byte[width][heigth];
				for (int i = 0 ;i < width; i++)
					for (int j = 0 ;j < heigth; j++)
						bmiMask[i][j] = booleanTbyte(mask.getPixel(i, j));
				for( byte[] tb : bmiMask)
					buf.write(tb);
			}
			
			if(skeletonflag) { 
				//write the skeleton into BMI format
			}	
			if(informationflag) {
				buf.write((byte) examen.getPatientInfos().size());
				//ici corriger les clés pour récupérer les infos du patients
				
				String[] patients_infos_titles = {"Patient ID","Patient birthday", "Patient age",
				"Patient sex", "Patient size" , "Patient weight", "Image position patient",
				"Image orientation patient"};
				for (String s : patients_infos_titles) {
					addFieldIntoBuf(s, examen.getPatientInfos().get(s), buf);
				}
				
				String[] study_data = { "Study data","Study date", "Study time", "Study description",
				"High bit", "Intercept", "Slope", "Window center", "Window width", "Slice thickness", "WindowCenterWidthExplanation"};
				for (String s : patients_infos_titles)
					addFieldIntoBuf(s, examen.getPatientInfos().get(s), buf);
				/*besoin de manufacturer
				*/
				
				fos.write(buf.toByteArray());
			}
	
		} catch(FileNotFoundException f) {
			//afficher que filename n'existe pas
			return false;
		}
		
		return true;

}

	public void load(String filename, Examen examen) {
		// TODO Auto-generated method stub
		
	}
	
	private byte booleanTbyte(boolean b) {
		return (byte)(b?1:0);
	}
	
	private void addFieldIntoBuf(String title, String description, ByteArrayOutputStream buf) {
		buf.write((byte) (title.length() + 1));
		for (byte b : title.getBytes())
			buf.write(b);
		buf.write((byte) (description.length()));
		for (byte b : description.getBytes())
			buf.write(b);
	}
}
