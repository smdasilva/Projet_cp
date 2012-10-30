package org.bdx1.diams.caching;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.bdx1.diams.Factory;
import org.bdx1.diams.model.BaseSlice;
import org.bdx1.diams.model.DefaultModelFactory;
import org.bdx1.diams.model.Slice;

import android.support.v4.util.LruCache;

class SliceCacher {

    protected class CacheLine {
        private Slice cachedSlice;
        private CountDownLatch lock;
        
        CacheLine(final File sourceFile) {
            lock = new CountDownLatch(1);
            new Thread(new Runnable() {
                
                public void run() {
                    chargeSlice(sourceFile);                    
                }
            }).run();
        }
        
        private void chargeSlice(final File sourceFile) {
            cachedSlice = Factory.MODEL_FACTORY.makeSlice(sourceFile);
            lock.countDown();
        }
        
        public Slice getSlice() {
            try {
                lock.await();
            } catch (InterruptedException e) {
                return null;
            }
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
