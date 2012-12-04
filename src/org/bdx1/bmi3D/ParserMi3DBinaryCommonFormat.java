package org.bdx1.bmi3D;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

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

		boolean dataflag = (examen.getNumberOfSlices() > 0) ;
		boolean volumeflag = examen.getSlice(0).getMask() != null;
		boolean skeletonflag = false; 
		boolean informationflag = !(examen.getPatientInfos().isEmpty() && examen.getStudyInfos().isEmpty());

		Slice slice = examen.getSlice(0);

		int width = slice.getImage().getWidth();
		int heigth = slice.getImage().getHeight();
		int depth = examen.getNumberOfSlices() + 1;

		float resolutionX = default_resolution;
		float resolutionY = default_resolution;
		float resolutionZ = default_resolution;

		DataOutputStream dos = null;
		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			dos = new DataOutputStream(bos);

			//Header
			//check if data is present
			dos.writeBoolean(dataflag);
			dos.writeBoolean(volumeflag);
			dos.writeBoolean(skeletonflag);
			dos.writeBoolean(informationflag);

			dos.writeInt(width);
			dos.writeInt(heigth);
			dos.writeInt(depth);
			dos.writeFloat(resolutionX);
			dos.writeFloat(resolutionY);
			dos.writeFloat(resolutionZ);

			if (dataflag) {
				for (int i = 0; i <  examen.getNumberOfSlices() ; i++) {
					for ( int j : examen.getSlice(i).getImage().getData())
						dos.writeInt(j);
				}
			}

			if (volumeflag) {
				for (int i =0 ; i < width ; i++)
					for (int j =0 ; j < heigth ; j++)
						dos.writeBoolean(examen.getSlice(0).getMask().getPixel(i, j));
			}

			if(skeletonflag) { 
				//write the skeleton into BMI format
			}

			if(informationflag) {
				Map<String,String> patientInfos = examen.getPatientInfos();
				Map<String,String> studyInfos = examen.getStudyInfos();

				dos.writeInt(patientInfos.size());


				for (String s : patientInfos.keySet()) {
					addFieldIntoBuf(s, patientInfos.get(s), dos);
				}


				for (String s : studyInfos.keySet())
					addFieldIntoBuf(s, studyInfos.get(s), dos);

				String s = "Patient Name";
				addFieldIntoBuf(s, patientInfos.get(s), dos);
				addFieldIntoBuf("Slice Number", String.valueOf(examen.getNumberOfSlices()), dos);

				dos.writeBytes("manufacturer");
				dos.writeInt(0);

				String[] resolutions = {"scan resolution", "pixel resolution x", "pixel resolution y"};
				for (String is : resolutions) {
					dos.writeInt(is.length() + 1);
					dos.writeBytes(is);
					dos.writeFloat(default_resolution);
				}


			}
		} catch(FileNotFoundException f) {
			//afficher que filename n'existe pas
			return false;
		}
		catch(IOException io) {
			return false;
		}
		finally {
			try {
				dos.flush();
				dos.close();
			} catch (IOException e) {
				return false;
			}
		}

		return true;

	}

	public void load(String filename, Examen examen) {
		// TODO Auto-generated method stub

	}

	private void addFieldIntoBuf(String title, String description, DataOutputStream buf) throws IOException {
		buf.writeInt(title.length() + 1);
		buf.writeBytes(title);
		buf.writeInt(description.length() + 1);
		buf.writeBytes(description);
	}
}
