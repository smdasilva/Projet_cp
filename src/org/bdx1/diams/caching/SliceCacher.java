package org.bdx1.diams.caching;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.bdx1.diams.Factory;
import org.bdx1.diams.model.Slice;

import android.support.v4.util.LruCache;

/**
 * This class implements a caching system for Slices.
 * It uses a LruCache from android libraries.
 * Each Slice is put in the cache using an internal
 * CacheLine class to handle thread synchronization.
 */
class SliceCacher {

    /**
     * Internal class to put Slices in the cache.
     * It handles synchronization when a thread tries
     * to access a Slice which is not yet in memory.
     * A CountDownLatch initialized to 1 is used for that.
     * When the Slice is charged, the Latch count is decreased.
     * Every thread waiting for the slice is then released.
     */
    protected class CacheLine {
        private Slice cachedSlice;
        private CountDownLatch lock;
        
        CacheLine(final File sourceFile) {
            lock = new CountDownLatch(1);
            new Thread(new Runnable() {
                
                public void run() {
                    chargeSlice(sourceFile);                    
                }
            }).start();
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
    
    /** Android class implementing a LRU algorithm for deletion **/
    private LruCache<File,CacheLine> internalCache;
    
    public SliceCacher(int maxSize) {
        internalCache = new LruCache<File, CacheLine>(maxSize);
    }
    
    /**
     * Recover a Slice from the specified file.
     * If the Slice is in cache, the function
     * returns the cached value, if not, the slice
     * is charged in cached before being returned.
     * @param sliceSource The File from wich to charge the Slice
     * @return The charged Slice
     */
    public Slice getSlice(File sliceSource) {
        CacheLine found = internalCache.get(sliceSource);
        if (found == null) {
            found = new CacheLine(sliceSource);
            internalCache.put(sliceSource, found);
        }
        return found.getSlice();
    }
    
    /**
     * Asynchronously charges the Slice in the cache
     * if it is not already charged.
     * @param sliceSource The Flie from wich to charge the Slice
     */
    public void chargeInCache(File sliceSource) {
        CacheLine found = internalCache.get(sliceSource);
        if (found == null)
            internalCache.put(sliceSource, new CacheLine(sliceSource));
    }
    
    /**
     * The maximum number of Slices in the cache
     * @return The maximum number of Slices in the cache
     */
    public int maxSize() {
        return internalCache.maxSize();
    }
    
    /**
     * The actual number of Slices in the cache.
     * @return Number of Slices in the cache
     */
    public int size() {
        return internalCache.size();
    }
}
