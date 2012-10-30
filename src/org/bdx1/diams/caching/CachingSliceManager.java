package org.bdx1.diams.caching;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bdx1.diams.model.Slice;
import org.bdx1.diams.model.SliceManager;

/**
 * This class speeds up slice handling by caching
 * slices likely to be asked soon.
 * @author Alexandre Perrot
 *
 */
public class CachingSliceManager implements SliceManager {

    private final static int MAX_CACHE_SIZE = 5;
    private final static int CACHE_REACH = 2;
    
    private List<File> files = new LinkedList<File>();
    private SliceCacher cache = new SliceCacher(MAX_CACHE_SIZE);
    
    public void addSlice(File source) {
        files.add(source);
    }

    public Slice getSlice(int i) {
        for (int n=0 ; n<=CACHE_REACH ; n++) {
            if (i+n < files.size())
                cache.chargeInCache(files.get(i+n));
            if (i-n >= 0)
                cache.chargeInCache(files.get(i-n));
        }
        return cache.getSlice(files.get(i));
    }

    public int numberOfSlices() {
        return files.size();
    }

}
