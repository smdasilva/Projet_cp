package org.bdx1.diams.caching;

import java.io.File;

import org.bdx1.diams.model.BaseSlice;
import org.bdx1.diams.model.Slice;


import android.support.v4.util.LruCache;

class SliceCacher {

    protected class CacheLine {
        private Slice cachedSlice;
        
        CacheLine(File sourceFile) {
            chargeSlice(sourceFile);
        }
        
        private void chargeSlice(File sourceFile) {
            new Thread(new Runnable() {
                
                public void run() {
                    cachedSlice = new BaseSlice(sourceFile);
                }
            }).run();
        }
    }
    
    private LruCache<File, BaseSlice> internalCache;
    
    public SliceCacher(int maxSize) {
        internalCache = new LruCache<File, BaseSlice>(maxSize) {
            protected BaseSlice create(File key) {
                return new BaseSlice(key);
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
