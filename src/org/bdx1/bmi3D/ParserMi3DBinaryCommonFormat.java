package org.bdx1.bmi3D;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bdx1.diams.model.Examen;
import org.bdx1.diams.model.Mask;
import org.bdx1.diams.model.Slice;


public class ParserMi3DBinaryCommonFormat implements ParserMi3DBinary {

	private final float default_resolution = 1;

	private String extension;

	public ParserMi3DBinaryCommonFormat() {
		extension = "bmi";
	}

	public boolean save(String filename, Examen examen) {
		filename = filename.trim();
		filename += extension;

		boolean dataflag = false;
		boolean volumeflag = (examen.getSlice(0).getMask() == null); //il me faut le masque
		boolean skeletonflag = false; //le skeleton si vous l'avez
		boolean informationflag = (!examen.getPatientInfos().isEmpty());

		Slice slice = examen.getSlice(0);

		int width = slice.getImage().getWidth();
		int heigth = slice.getImage().getHeight();
		int depth = examen.getNumberOfSlices() + 1;

		float resolutionX = default_resolution;
		float resolutionY = default_resolution;
		float resolutionZ = default_resolution;


		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			//ByteArrayOutputStream buf = new ByteArrayOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			DataOutputStream dos = new DataOutputStream(bos);

			//Header
			dos.writeBoolean(dataflag);
			dos.writeBoolean(volumeflag);
			dos.writeBoolean(skeletonflag);
			dos.writeBoolean(informationflag);

			dos.writeInt(width);
			dos.writeInt(width);
			dos.writeInt(heigth);
			dos.writeInt(depth);
			dos.writeFloat(resolutionX);
			dos.writeFloat(resolutionY);
			dos.writeFloat(resolutionZ);

			if (volumeflag) {
				Mask mask = examen.getSlice(0).getMask();
				for (int i = 0 ;i < width; i++)
					for (int j = 0 ;j < heigth; j++)
						dos.writeBoolean(mask.getPixel(i, j));
			}

			if(skeletonflag) { 
				//write the skeleton into BMI format
			}	
			if(informationflag) {
				dos.writeInt(examen.getPatientInfos().size());
				//ici corriger les clés pour récupérer les infos du patients

				String[] patients_infos_titles = {"Patient ID","Patient birthday", "Patient age",
						"Patient sex", "Patient size" , "Patient weight", "Image position patient",
				"Image orientation patient"};
				for (String s : patients_infos_titles) {
					addFieldIntoBuf(s, examen.getPatientInfos().get(s), dos);
				}

				String[] study_data = { "Study data","Study date", "Study time", "Study description",
						"High bit", "Intercept", "Slope", "Window center", "Window width", "Slice thickness", "WindowCenterWidthExplanation"};
				for (String s : patients_infos_titles)
					addFieldIntoBuf(s, examen.getPatientInfos().get(s), dos);
				/*besoin de manufacturer
				 */

				for (String s : study_data)
					addFieldIntoBuf(s, examen.getPatientInfos().get(s), dos);

				String s = "Patient Name";
				addFieldIntoBuf(s, examen.getPatientInfos().get(s), dos);
				addFieldIntoBuf("Slice Number", String.valueOf(examen.getNumberOfSlices()), dos);

				dos.writeBytes("manufacturer");
				dos.writeInt(0);

				String[] resolutions = {"scan resolution", "pixel resolution x", "pixel resolution x"};
				for (String is : resolutions) {
					dos.writeInt(is.length() + 1);
					dos.writeBytes(is);
					dos.writeFloat(default_resolution);
				}

				fos.close();
				bos.close();
				dos.close();
			}
		} catch(FileNotFoundException f) {
			//afficher que filename n'existe pas
			return false;
		}
		catch(IOException io) {
			return false;
		}

		return true;

	}

	public void load(String filename, Examen examen) {
		// TODO Auto-generated method stub

	}

	private void addFieldIntoBuf(String title, String description, DataOutputStream buf) throws IOException {
		buf.writeInt(title.length() + 1);
		buf.writeBytes(title);
		buf.writeInt(description.length());
		buf.writeBytes(description);
	}
}
