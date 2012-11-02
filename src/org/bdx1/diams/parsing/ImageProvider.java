package org.bdx1.diams.parsing;

import java.io.File;

import org.bdx1.diams.model.Image;

public interface ImageProvider {

    public boolean read(File sourceFile);
    public Image getImage();

}
