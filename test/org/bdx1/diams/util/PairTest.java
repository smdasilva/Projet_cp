package org.bdx1.diams.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PairTest {

	private Pair<Integer,Integer> p0;
	private Pair<Integer,Integer> p1;
	private Pair<Integer,Integer> p2;
	private final static int response = 42;
	private final static int time = 1337;



	@Before
	public void setUp() throws Exception {
		p0 = new Pair<Integer, Integer>(0,0);
		p1 = new Pair<Integer,Integer>(response,0);
		p2 = new Pair<Integer,Integer>(time,response);
	}

	

	@Test
	public void testPair() {
		int x = 5;
		int y = 4;
		Pair<Integer, Integer> pair = new Pair<Integer, Integer>(x,y);
		assertTrue(pair.getFirst() == x);
		assertTrue(pair.getSecond() == y);
	}

	@Test
	public void testEqualsObject() {
		Pair<Integer, Integer> f = new Pair<Integer, Integer>(8, 9);
		assertFalse(p0.equals(f));
		assertFalse(p0.equals(new Pair<Integer, Integer>(9, 8)));
		assertTrue(f.equals(new Pair<Integer,Integer>(8,9)));
	}

	@Test
	public void testToString() {
		assertEquals("(0,0)",p0.toString());
	}

	@Test
	public void testGetFirst() {
		assertTrue(response == p1.getFirst());
		assertFalse( response == p0.getFirst());
		assertTrue(time == p2.getFirst());
	}

	@Test
	public void testSetFirst() {
		int val0 = 14;
		int val1 = 47;
		int firstorigin = p1.getFirst();
		assertFalse(p1.getFirst() == val0 && p1.getFirst() == val1);
		p1.setFirst(val0);
		assertFalse(p1.getFirst() == val1 && firstorigin == p1.getFirst());
		assertTrue(p1.getFirst() == val0);
	}

	@Test
	public void testGetSecond() {
		assertTrue(0 == p1.getSecond());
		assertFalse( response == p0.getSecond());
		assertTrue(response == p2.getSecond());
	}

	@Test
	public void testSetSecond() {
		int val0 = 14;
		int val1 = 47;
		int secondorigin = p1.getSecond();
		assertFalse( p1.getSecond() == val0 && p1.getSecond() == val1);
		p1.setSecond(val1);
		assertFalse(p1.getSecond() == val0 && p1.getSecond() == secondorigin);
		assertTrue(p1.getSecond() == val1);
	}

}
