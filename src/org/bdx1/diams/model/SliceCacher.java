package org.bdx1.diams.model;

import java.io.File;


import android.support.v4.util.LruCache;

class SliceCacher {

    private LruCache<File, Slice> internalCache;
    
    public SliceCacher(int maxSize) {
        internalCache = new LruCache<File, Slice>(maxSize) {
            protected Slice create(File key) {
                return new Slice(key);
            }
        };
    }
    
    public Slice getSlice(File sliceSource) {
        return internalCache.get(sliceSource);
    }
    
    public void chargeInCache(File sliceSource) {
        internalCache.get(sliceSource);
    }
    
    public int maxSize() {
        return internalCache.maxSize();
    }
    
    public int size() {
        return internalCache.size();
    }
}
