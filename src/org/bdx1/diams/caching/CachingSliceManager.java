package org.bdx1.diams.caching;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bdx1.diams.model.Slice;
import org.bdx1.diams.model.SliceManager;

public class CachingSliceManager implements SliceManager {

    private final static int MAX_CACHE_SIZE = 5;
    
    private List<File> files = new LinkedList<File>();
    private SliceCacher cache = new SliceCacher(MAX_CACHE_SIZE);
    
    public void addSlice(File source) {
        files.add(source);
    }

    public Slice getSlice(int i) {
        return cache.getSlice(files.get(i));
    }

    public int numberOfSlices() {
        return files.size();
    }

}
