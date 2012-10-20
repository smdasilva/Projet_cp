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
        
        
    }

    @Override
    public boolean saveFile(String filename, Examen exam, List<Boolean> options) {
	// TODO Auto-generated method stub
	return false;
    }

}
