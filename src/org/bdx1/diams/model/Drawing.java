package org.bdx1.diams.model;

import org.bdx1.diams.util.Point2D;

public class Drawing {
	
	Mask mask;
	
	public Drawing(Mask m) {
		this.mask = m;
	}
	
	public void draw(int x, int y) {
		mask.setPixel(x, y, true);
	}
	
	public void draw(Point2D p) {
		int x = (int)p.x;
		int y = (int)p.y;
		mask.setPixel(x, y, true);
	}
	
}
