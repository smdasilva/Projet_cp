package org.bdx1.diams.model;

public interface Mask {
	public void setPixel(int x, int y, boolean isSelected);

	public boolean getPixel(int x, int y);
}
