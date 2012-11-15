package org.bdx1.diams.model;

import java.util.Iterator;

import org.bdx1.diams.util.Pair;

public class MatrixMask implements Mask {

    int[][] mask;
    int width, height;
    
    
    public MatrixMask(int w, int h) {
        width = w;
        height = h;
    }
    
    public Iterator<Pair<Integer, Integer>> iterator() {
        return null;
    }

    public void setPixel(int x, int y, boolean isSelected) {

    }

    public boolean getPixel(int x, int y) {
        // TODO Auto-generated method stub
        return false;
    }

    public int[] getData() {
        // TODO Auto-generated method stub
        return null;
    }

}
