package util.parser;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.CharBuffer;

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

	

    public void loadFile(String filename, Examen exam) {
    	
        /* (VERSION 2)
        byte[] buffer = new byte[1024];
    	FileInputStream fis = new FileInputStream(new File(filename));
    	DataInputStream dis = new DataInputStream(fis);
    	BufferedInputStream bis = new BufferedInputStream(dis);
    	int bytesRead = 0;
    	*/
    	
    	boolean volumeFlag, skeletonFlag, skeletonSlicesFlag;
        int sliceWidth, sliceHeight, nbSlice;
        float  resolutionX, resolutionY, resolutionZ;
        int slope, intercept, Wmin,Wmax;
         
        DataInputStream lecteur;
        
        lecteur=
          new DataInputStream(new BufferedInputStream
    			  (new FileInputStream(filename)));
        
    	volumeFlag = lecteur.readBoolean();
    	skeletonFlag = lecteur.readBoolean();
    	skeletonSlicesFlag = lecteur.readBoolean();
    	
    	sliceWidth = lecteur.readInt();
    	sliceHeight = lecteur.readInt();
    	nbSlice = lecteur.readInt();
    	
    	resolutionX = lecteur.readFloat();
    	resolutionY = lecteur.readFloat();
    	resolutionZ = lecteur.readFloat();

    	slope = lecteur.readInt();
    	intercept = lecteur.readInt();
    	Wmin = lecteur.readInt();
    	Wmax = lecteur.readInt();

        int size =sliceWidth*sliceHeight*nbSlice;
        exam.setResolution( resolutionX,  resolutionY,  resolutionZ);
        exam.setWindowingRange(Wmin,Wmax);
        exam.setParametersSI( slope,  intercept);
    	
        // NEW EXAMEN

        //-------------------------------
        // VOLUME
        //-------------------------------
        if (volumeFlag)
        {
        	// Attention, il faudra transformer les byte en char !
            byte[] maskvolume = new byte[size];
            int sizeOfChar = 2;
            lecteur.readFully(maskvolume, 0, sizeOfChar * size);
            Mask volume=new Mask();
            volume.setParameters( sliceWidth, sliceHeight, nbSlice);
            volume.setRefSrc(maskvolume);
            exam.setRefPixelMask(maskData);
        }

        //-------------------------------
        // SKELETON
        //-------------------------------
        if (skeletonFlag)
        {
            Skeleton skeleton = exam.getSkeleton();

            // Nodes
            int nNode;
            nNode =lecteur.readInt();
            for (int i=0 ; i < nNode ; i++)
            {
                float x, y, z;
                x = lecteur.readFloat();
                y = lecteur.readFloat();
                z = lecteur.readFloat();

                Node node = new Node(x, y, z);
                
                flagview = lecteur.readBool();
                parent = lecteur.readInt();

                int nChild;
                nChild = readInt();
                
                for (int j=0 ; j < nChild ; j++)
                {
                    int iChild;
                    iChild = lecteur.readInt();
                    node.childrens.add(iChild);
                }

                skeleton.addNode(node);
            }

            // Branches
            int nBranch;
            nBranch = lecteur.readInt();
            for (int i=0 ; i < nBranch ; i++)
            {
                Branch branch = new Branch();
                boolean flagView = lecteur.readBoolean();
                int parent = lecteur.readInt();
                int generation = lecteur.readInt();
                float length = lecteur.readFloat();
                double branch.angle.rho = lecteur.readDouble();
                double branche.angle.theta = lecteur.readDouble();
                
                int size;
                size = lecteur.readInt();

                char cstr = new char[size];
                cstr = lecteur.readChar();
                branch.name = string(cstr);
                delete []cstr;

                int nChild;
                nChild = lecteur.readInt();

                for (int j=0 ; j < nChild ; j++)
                {
                    int iChild;
                    iChild = lecteur.readInt();
                    branch.childrens.add(iChild);
                }

                int nNode;
                nBodes ) lecteur.readInt();

                for (int j=0 ; j < nNode ; j++)
                {
                    int iNode;
                    iNodes = lecteur.readInt();
                    branch.nodes.add(iNode);
                }

                skeleton.addBranch(branch);
            }

            // SKELETON SLICES

            if (skeletonSlicesFlag)
            {
                for (int i=0 ; i < skeleton.getBranchesSize() ; i++)
                {
                    Branch branch = skeleton.getBranch(i);

                    // Slices coord
                    int nSlice;
                    nbSlice= lecteur.readInt();
                    for (int j=0 ; j < nSlice ; j++)
                    {
                        SkeletonSlice skelSlice = new SkeletonSlice();
                        int w, h;
                        w = lecteur.readInt();
                        
                        // Test : ajout de la résolution de l'image ancien / nouveau format bmi3d
                        if (w == -1)
                        {
                            float resx, resy;
                            resx = lecteur.readFloat();
                            skelSlice.resolution.x = resx;
                            resy = lecteur.readFloat();
                            skelSlice.resolution.y = resy;
                            w = lecteur.readInt();
                        }
                        h = lecteur.readInt();

                        skelSlice.coord = new ImageData(w,h);

                        for (int k=0 ; k < w * h ; k++)
                        {
                            int x, y, z;
                            x = lecteur.readInt():
                            y = lecteur.readInt();
                            z = lecteur.readInt();
                            
                            skelSlice.coord.setData(k, Point3D_t(x, y, z));
                        }

                        branch.slices.add(skelSlice);
                    }

                    // Slices image
                    nSlice = lecteur.readInt();

                    for (int j=0 ; j < nSlice ; j++)
                    {
                        SkeletonSlice skelSlice = branch.slices.get(j);

                        int w, h;
                        int flag, slope, intercept;
                        flag = lecteur.readInt();

                        // Test : ajout du slope / intercept de l'image ancien / nouveau format bmi3d
                        if (flag == -1)
                        {
                            slope = lecteur.readInt();
                            intercept = lecteur.readInt();
                            w = lecteur.readInt();
                        }
                        else
                            w = flag;
                        h = lecteur.readInt();

                        skelSlice.image = new ImageFloat(w, h);
                        if (flag == -1)
                        {
                            skelSlice.image.setSlope(slope);
                            skelSlice.image.setIntercept(intercept);
                        }

                        // image
                        float floatPtrVal = new float[w * h];
                        int sizeOfFloat = 4;
                        imageReader.read((char)floatPtrVal, sizeOfFloat * w * h);
                        skelSlice.image.setSrc(floatPtrVal);

                        // mask
                        char charPtrVal = new char[w * h];
                        imageReader.read((char )charPtrVal, sizeOfFloat * w * h);
                        skelSlice.image.getMask().setSrc(charPtrVal);
                    }
                }
            }

        }


        //-------------------------------
        // DATA
        //-------------------------------

        float maskData = new float[size];
        imageReader.read((float)maskData, sizeof(float) * size);
        Mask data=new  Mask ();
        data.setParameters( sliceWidth, sliceHeight, nbSlice);
        data.setRefSrc(maskData);
        exam.setRefPixelMask(maskData);
        exam.setRefPixel(maskData);


        //-------------------------------
        //MAX
        //-------------------------------
        float TabMax = new float[nbSlice];
        imageReader.read((float)TabMax, sizeof(float) * nbSlice);
        exam.setRefPixel(maskData);


        //-------------------------------
        //Informations examens et pour la liste d'image //A FAIRE
        //-------------------------------

        lecteur.close();
        string filenameTitle(filename,
                             filename.find_last_of('/')+1,
                             filename.find_last_of('.') -  filename.find_last_of('/') -1);
        exam.setFilename(filenameTitle);

    }
}
        
        

    @Override
    public boolean saveFile(String filename, Examen exam, List<boolean> options) {

    	DataOutputStream imageWriter;
        
        lecteur=
          new DataOutputStream(new BufferedOutputStream
    			  (new FileOutputStream(filename)));  
    	
    	
              boolean boolVal;
              int intVal, intVal2;
              float floatVal;
              char charPtrVal;
              float floatPtrVal;


              //-------------------------------
              // ENTETE
              //-------------------------------

              boolVal = options.get(0);
              imageWriter.writeBoolean(boolVal);
              boolVal = options.get(1);
              imageWriter.writeBoolean(boolVal);
              boolVal = options.get(2);
              imageWriter.writeBoolean(boolVal);

              intVal = examen.getWidth();
              imageWriter.writeInt(intVal);
              intVal = examen.getHeight();
              imageWriter.writeInt(intVal);
              intVal = examen.getSize();
              imageWriter.writeInt(intVal);

              imageWriter.writeBoolean(floatVal);
              floatVal = examen.getResolutionX();
              imageWriter.writeFloat(floatVal);
              floatVal = examen.getResolutionY();
              imageWriter.writeFloat(floatVal);
              floatVal = examen.getResolutionZ();


              imageWriter.writeFloat(floatVal);
              floatVal = examen.getSlope();
              imageWriter.writeInt(floatVal);
              floatVal = examen.getIntercept();
              imageWriter.writeInt(floatVal);
              floatVal = examen.getWindowingMin();
              imageWriter.writeInt(floatVal);
              floatVal = examen.getWindowingMax();


              //-------------------------------
              // VOLUME
              //-------------------------------

              if (options.get(0))
              {

                  Mask mask = examen.getMasqueRef();
                  charPtrVal = mask.getSrcRef();
                  int size = examen.getWidth()
                             * examen.getHeight() * examen.getDepth();

                  imageWriter.writeInt(charPtrVal);
              }

              //-------------------------------
              // SKELETON
              //-------------------------------

              if (options.get(1))
              {
                  Skeleton skeleton = examen.getSkeleton();

                  // Nodes
                  intVal = skeleton.getNodesSize();
                  imageWriter.writeInt(intVal);
                  for (int i=0 ; i < intVal ; i++)
                  {
                      Node node = skeleton.getNode(i);

                      imageWriter.writeFloat(node.x);
                      imageWriter.writeFloat(node.y);
                      imageWriter.writeFloat(node.z);
                      imageWriter.writeBoolean(node.flagview);
                      imageWriter.writeInt(node.parent);

                      intVal2 = node.childrens.size();
                      imageWriter.writeInt(intVal2);
                      for (int j=0 ; j < node.childrens.size() ; j++)
                      {
                          intVal2 = node.childrens.get(j);
                          imageWriter.writeInt(intVal2);
                      }
                  }

                  // Branches
                  intVal = skeleton.getBranchesSize();
                  imageWriter.writeInt(intVal);
                  for (int i=0 ; i < intVal ; i++)
                  {
                      Branch branch = skeleton.getBranch(i);

                      imageWriter.writeBoolean(branch.flagView);
                      imageWriter.writeInt(branch.parent);
                      imageWriter.writeInt(branch.generation);
                      imageWriter.writeFloat(branch.length);
                      imageWriter.writeDouble(branch.angle.rho);
                      imageWriter.writeDouble(branch.angle.theta);

                      intVal2 = (int)branch.name.size()+1;
                      imageWriter.writeInt(intVal2);
                      imageWriter.writeChar(branch.name.c_str());

                      intVal2 = branch.childrens.size();
                      imageWriter.writeInt(intVal2);
                      for (int j=0 ; j < branch.childrens.size() ; j++)
                      {
                          intVal2 = branch.childrens.get(j);
                          imageWriter.writeInt(intVal2); //intVal2, sizeof(int)
                      }

                      intVal2 = branch.nodes.size();
                      imageWriter.writeInt(intVal2);
                      for (int j=0 ; j < branch.nodes.size() ; j++)
                      {
                          intVal2 = branch.nodes.get(j);
                          imageWriter.writeInt(intVal2);
                      }
                  }

                  // SKELETON SLICES

                  if (options.get(2))
                  {
                      for (int i=0 ; i < skeleton.getBranchesSize() ; i++)
                      {
                          Branch branch = skeleton.getBranch(i);

                          // Slices coord
                          intVal = branch.slices.size();
                          imageWriter.writeInt(intVal);
                          for (int j=0 ; j < intVal ; j++)
                          {
                              SkeletonSlice skelSlice = branch.slices.get(j);

                              // Test : ajout de la résolution de l'image ancien / nouveau format bmi3d
                              intVal2 = -1;
                              imageWriter.writeInt(intVal2);
                              imageWriter.writeFloat(skelSlice.resolution.x);
                              imageWriter.writeFloat(skelSlice.resolution.y);

                              ImageData<Point3D_t<int> > coord = skelSlice.coord;
                              intVal2 = coord.getWidth();
                              imageWriter.writeInt(intVal2);
                              intVal2 = coord.getHeight();
                              imageWriter.writeInt(intVal2);

                              for (int k=0 ; k < coord.getWidth() * coord.getHeight() ; k++)
                              {
                                  Point3D_t<int> p = coord.getData(k);
                                  imageWriter.writeInt(p.x);
                                  imageWriter.writeInt(p.y);
                                  imageWriter.writeInt(p.z);
                              }
                          }

                          // Slices image
                          intVal = branch.slices.size();
                          imageWriter.writeInt(intVal);
                          for (int j=0 ; j < intVal ; j++)
                          {
                              Image image = branch.slices.get(j).image;

                              // Test : ajout de la résolution de l'image ancien / nouveau format bmi3d
                              intVal2 = -1;
                              imageWriter.writeInt(intVal2);
                              intVal2 = image.getSlope();
                              imageWriter.writeInt(intVal2);
                              intVal2 = image.getIntercept();
                              imageWriter.writeInt(intVal2);

                              intVal2 = image.getWidth();
                              imageWriter.writeInt(intVal2);
                              intVal2 = image.getHeight();
                              imageWriter.writeInt(intVal2);

                              int size = image.getWidth() * image.getHeight();

                              // image
                              floatPtrVal = image.getSrc();
                              imageWriter.writeFloat(floatPtrVal); //sizeof(float) * size

                              // mask
                              charPtrVal = image.getMask().getSrc();
                              imageWriter.writeChar(charPtrVal); //sizeof(char) * size
                          }
                      }
                  }

              }

              //-------------------------------
              // DATA
              //-------------------------------

              Mask data = examen.getDataRef();
              floatPtrVal = data.getSrcRef();
              int size = examen.getWidth()
                         * examen.getHeight() * examen.getDepth();

              imageWriter.writeChar(charPtrVal); // sizeof(char) *size

              //-------------------------------
              //MAX
              //-------------------------------
              int sizetab=examen.getDepth();
              imageWriter.writeFloat(examen.getTabMax()); // sizeof(float) *size

              //-------------------------------
              //Informations examens et pour la liste d'image //A FAIRE
              //-------------------------------

              imageWriter.close();

              return true;

      }
    	


