package org.bdx1.diams.model;

import java.util.Set;

import org.bdx1.diams.util.Pair;

public interface Mask extends Iterable<Pair<Integer,Integer>> {
	public void setPixel(int x, int y, boolean isSelected);

	public boolean getPixel(int x, int y);
	
	public int[] getData();
}
