package org.bdx1.bmi3D;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import org.bdx1.commons.ExternalStorage;
import org.bdx1.diams.model.Examen;


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
		boolean volumeflag = examen.getMask() == null; //il me faut le masque
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
			
			if (volumeflag)
				//besoin du mask
			if(skeletonflag) {
			//il me faut le skeleton
			}
			if(informationflag) {
				buf.write((byte) examen.getPatientInfos().size());
				//ici corriger les clés pour récupérer les infos du patients
				String[] patients_infos_titles = { "patient_id", "patient_birthday", "patient_age",
				"patient_sex", "patient_size" , "patient_weight"};
				for (String s : patients_infos_titles)
					addFieldIntoBuf(s, examen.getPatientInfos().get(s), buf);
				
				String[] study_data = { "study_date", "study_time", "study_description"};
				for (String s : patients_infos_titles)
					addFieldIntoBuf(s, examen.getPatientInfos().get(s), buf);
				/*besoin de high bit intercept, slope, window-center, slice thickness,
					window-center width exploration
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
