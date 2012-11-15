package org.bdx1.diams.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bdx1.diams.util.CantorPair;
import org.bdx1.diams.util.Pair;

import android.text.InputFilter.LengthFilter;

public class InfiniteMask implements Mask {

	private Set<Integer> mask;
	private int height;
	private int width;
	
	
	public InfiniteMask(int w, int h) {
		mask = new LinkedHashSet<Integer>();
		this.height = h;
		this.width = w;
	}

	public InfiniteMask(boolean[][] mask) {
		this(mask.length, mask[0].length);
		for(int i = 0; i < mask.length ; i++)
			for(int j = 0; j < mask[i].length ; j++)
				setPixel(i,j, mask[i][j]);

	}

	public void setPixel(int x, int y, boolean isSelected) {
		assert(x >= 0 && y >= 0);
		int cantorValue = CantorPair.encode(x, y);
		if(isSelected) {
			mask.add(cantorValue);
		}

		else {
			if(mask.contains(cantorValue))
				mask.remove(cantorValue);
		}

	}

	public boolean getPixel(int x, int y) {
		assert(x >= 0 && y >= 0);
		return mask.contains(CantorPair.encode(x, y));
	}
	
    public Iterator<Pair<Integer, Integer>> iterator() {
        
        return new CantorIterator(mask);
    }

    private class CantorIterator implements Iterator<Pair<Integer, Integer>> {

        private Set<Integer> set;
        private Iterator<Integer> iter;
        private Pair<Integer, Integer> pair;

        public CantorIterator(Set<Integer> set) {
            this.set = set;
            iter = set.iterator();
            pair = new Pair<Integer, Integer>(0, 0);
        }
        
        public boolean hasNext() {
            return iter.hasNext();
        }

        public Pair<Integer, Integer> next() {
            CantorPair.decode(iter.next(), pair);
            return pair;
        }

        public void remove() {
            
        }
        
    }

    public int[] getData() {
        int[] res = new int[width*height];
        Arrays.fill(res, 0);
        for (Pair<Integer, Integer> p : this) {
            res[p.first+p.second*width] = 0xFFFFFFFF;
        }
        return res;
    }
    
}
