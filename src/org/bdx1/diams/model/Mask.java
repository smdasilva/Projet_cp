package org.bdx1.diams.model;

import java.util.Set;

public interface Mask {
	public void setPixel(int x, int y, boolean isSelected);

	public boolean getPixel(int x, int y);
	
	public Set<Integer> getMask();
}
