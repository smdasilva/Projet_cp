package org.bdx1.diams.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class CantorPairTest {

	private final static int nbloop = 100;
	private LinkedList<Integer> allencoded;

	@Before
	public void setUp() throws Exception {
		allencoded = new LinkedList<Integer>();
		
	}

	@Test
	public void testEncodeIntInt() {
		assertTrue(CantorPair.encode(0, 0) == 0);
		HashSet<Integer> result = storedEncodedValue();
		assertTrue(allencoded.size() == result.size()); // ensure there's no duplicate
	}

	private HashSet<Integer> storedEncodedValue() {
		boolean test;
		for(int i = 0; i < nbloop; i++)
			for(int j = 0; j < nbloop; j++) {
				int cantorresult = CantorPair.encode(i, j);
				test = CantorPair.encode(i, j) ==  CantorPair.encode(j, i);
				if( i!= j)
					assertFalse(test);
				else
					assertTrue(test);
				allencoded.add(cantorresult);
			}
		HashSet<Integer> result = new HashSet<Integer>(allencoded);
		return result;
	}
	
	@Test
	public void testDecode() {
		Pair<Integer,Integer> currentpair = new Pair<Integer,Integer>(0,0);
		for(int i= 0; i< nbloop ; i++)
			for(int j=0; j <nbloop ; j++) {
				int cantorresult = CantorPair.encode(i, j);
				currentpair.setFirst(i);
				currentpair.setSecond(j);
				assertEquals(currentpair, CantorPair.decode(cantorresult));
				
			}
	}

}
