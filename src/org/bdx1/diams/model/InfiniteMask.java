package org.bdx1.diams.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bdx1.diams.util.CantorPair;

public class InfiniteMask implements Mask {

	private Set<Integer> mask;

	public InfiniteMask() {
		mask = new LinkedHashSet<Integer>();
	}

	public InfiniteMask(boolean[][] mask) {
		this();
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
	
	public Set<Integer> getMask() {
		return mask;
	}

}
