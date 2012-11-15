package org.bdx1.diams.util;

//from http://sachiniscool.blogspot.fr/2011/06/cantor-pairing-function-and-reversal.html
public class CantorPair {

	/**
	 * @param args
	 */

	public static int encode(int x, int y) {
		return ((x+y) * (x + y +1)) / 2 + y;
	}
	
	public static int encode(int [] t) {
		assert (t.length == 2);
		return encode(t[0],t[1]);
	}
	
	public static int encode(Pair<Integer,Integer> p) {
		return encode(p.getFirst(),p.getSecond());
	}


	public static Pair<Integer,Integer> decode(int n) {
	        Pair<Integer, Integer> p = new Pair<Integer, Integer>(0, 0);
		
	        decode(n,p);
	        return p;
	}	
	
	public static void decode(int n, Pair<Integer, Integer> p) {
	    int t = (int)Math.floor((-1D + Math.sqrt(1D + 8 * n))/2D);
            int x = t * (t + 3) / 2 - n;
            int y = n - t * (t + 1) / 2;
            p.first = x;
            p.second = y;
	}
}
