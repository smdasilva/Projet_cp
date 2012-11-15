package org.bdx1.diams.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;



public class MaskTest {

	private Mask mask;
	private static final int nbloop = 100;
	private static final int width = 1280;
	private static final int height = 800;
	private boolean[][] allfalse;
	private boolean[][] alltrue;

	@Before
	public void setUp() throws Exception {
		mask = new InfiniteMask(width, height);
		allfalse = new boolean[width][height];
		alltrue = new boolean[width][height];
		for(int i = 0 ; i < width ; i++)
			for(int j = 0 ; j < height ; j++)
				alltrue[i][j] = true;
	}

	@Test
	public void testSetPixel() {
		InfiniteMask maskTrue = new InfiniteMask(alltrue);
		InfiniteMask maskFalse = new InfiniteMask(allfalse);
	}

	@Test
	public void testGetPixel() { 
		for(int i = 0 ; i < nbloop ; i++)
			for(int j = 0 ; j < nbloop ; j++) {
				assertFalse(mask.getPixel(i, j));
				mask.setPixel(i, j, true);
				assertTrue(mask.getPixel(i, j));
				mask.setPixel(i, j, false);
				assertFalse(mask.getPixel(i, j));
			}
	}

}
