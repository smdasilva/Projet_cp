package org.bdx1.diams.model;

import java.util.Arrays;
import java.util.Iterator;

import org.bdx1.diams.util.Pair;

public class MatrixMask implements Mask {

    int[] mask;
    int width, height;
    
    
    public MatrixMask(int w, int h) {
        width = w;
        height = h;
        mask = new int[w*h];
        Arrays.fill(mask, 0xFF0000FF);
    }
    
    public Iterator<Pair<Integer, Integer>> iterator() {
        return null;
    }

    public void setPixel(int x, int y, boolean isSelected) {
        mask[x+y*width] = (isSelected ? 0xFFFF0000 : 0xFF0000FF);
    }

    public boolean getPixel(int x, int y) {
        return mask[x+y*width] != 0;
    }

    public int[] getData() {
        return mask;
    }

}
