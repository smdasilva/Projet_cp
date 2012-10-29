package org.bdx1.diams.caching;

import java.io.File;

import org.bdx1.diams.model.BaseSlice;
import org.bdx1.diams.model.ModelFactory;
import org.bdx1.diams.model.Slice;

import android.support.v4.util.LruCache;

class SliceCacher {

    protected class CacheLine {
        private Slice cachedSlice;
        
        CacheLine(File sourceFile) {
            chargeSlice(sourceFile);
        }
        
        private synchronized void chargeSlice(final File sourceFile) {
            new Thread(new Runnable() {
                
                public void run() {
                    cachedSlice = ModelFactory.makeSlice(sourceFile);
                }
            }).run();
        }
        
        public synchronized Slice getSlice() {
            return cachedSlice;
        }
    }
    
    private LruCache<File,CacheLine> internalCache;
    
    public SliceCacher(int maxSize) {
        internalCache = new LruCache<File, CacheLine>(maxSize);
    }
    
    public Slice getSlice(File sliceSource) {
        CacheLine found = internalCache.get(sliceSource);
        if (found == null) {
            found = new CacheLine(sliceSource);
            internalCache.put(sliceSource, found);
        }
        return found.getSlice();
    }
    
    public void chargeInCache(File sliceSource) {
        CacheLine found = internalCache.get(sliceSource);
        if (found == null)
            internalCache.put(sliceSource, new CacheLine(sliceSource));
    }
    
    public int maxSize() {
        return internalCache.maxSize();
    }
    
    public int size() {
        return internalCache.size();
    }
}
