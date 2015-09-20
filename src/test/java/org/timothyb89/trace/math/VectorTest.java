package org.timothyb89.trace.math;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tim
 */
public class VectorTest {
	
	public static final double DELTA = 0.0001d;
	
	public VectorTest() {
	}
	
	/**
	 * Test of dot method, of class Vector.
	 */
	@Test
	public void testDot() {
		Vector a = Vector.of(1, 2, 3);
		Vector b = Vector.of(4, -5, 6);
		
		assertEquals(a.dot(b), 12, DELTA);
		assertEquals(b.dot(a), 12, DELTA);
	}

	/**
	 * Test of cross method, of class Vector.
	 */
	@Test
	public void testCross() {
		Vector a = Vector.of(3, -3, 1);
		Vector b = Vector.of(4, 9, 2);
		Vector c = Vector.of(-15, -2, 39);
		Vector d = Vector.of(-12, 12, -4);
		
		assertArrayEquals(c.data(), b.cross(a).data(), DELTA);
		assertArrayEquals(c.scale(-1).data(), a.cross(b).data(), DELTA);
		assertArrayEquals(Vector.zeroes(3).data(), d.cross(a).data(), DELTA);
	}

	/**
	 * Test of norm method, of class Vector.
	 */
	@Test
	public void testNormalize() {
		assertArrayEquals(
				new double[] {0, 0},
				Vector.of(0, 0).normalize().data(),
				DELTA);
		
		assertArrayEquals(
				new double[] {1, 0},
				Vector.of(1, 0).normalize().data(),
				DELTA);
		
		assertArrayEquals(
				new double[] {0.6, 0.8},
				Vector.of(3, 4).normalize().data(),
				DELTA);
	}
	
}
