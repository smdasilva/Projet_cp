package org.bdx1.diams.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ListSliceManager implements SliceManager {

    private List<Slice> slices = new LinkedList<Slice>();
    
    public void addSlice(File source) {
        slices.add(new BaseSlice(source));
    }

    public Slice getSlice(int i) {
        return slices.get(i);
    }

    public int numberOfSlices() {
        return slices.size();
    }

}
