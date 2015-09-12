package org.timothyb89.trace.math;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author timothyb
 */
@SuppressWarnings("unchecked")
public class MatrixTest {

	public static final double DELTA = 0.0001d;

	@Test
	public void testRowGet() throws Exception {
		Matrix m = Matrix.identity(3);
		assertArrayEquals(new double[] {1, 0, 0}, m.row(0), DELTA);
		assertArrayEquals(new double[] {0, 1, 0}, m.row(1), DELTA);
		assertArrayEquals(new double[] {0, 0, 1}, m.row(2), DELTA);

		Matrix b = Matrix.build(3)
				.row(1, 2, 3)
				.row(4, 5, 6)
				.row(7, 8, 9).get();

		assertArrayEquals(new double[] {1, 2, 3}, b.row(0), DELTA);
		assertArrayEquals(new double[] {4, 5, 6}, b.row(1), DELTA);
		assertArrayEquals(new double[] {7, 8, 9}, b.row(2), DELTA);
	}

	@Test
	public void testRowSet() throws Exception {
		Matrix m = Matrix.identity(3).row(1, 5, 6, 7);
		assertArrayEquals(new double[] {5, 6, 7}, m.row(1), DELTA);
		assertArrayEquals(new double[] {1, 5, 0}, m.col(0), DELTA);
	}

	@Test
	public void testColGet() throws Exception {
		Matrix a = Matrix.identity(3);
		assertArrayEquals(new double[] {1, 0, 0}, a.col(0), DELTA);
		assertArrayEquals(new double[] {0, 1, 0}, a.col(1), DELTA);
		assertArrayEquals(new double[] {0, 0, 1}, a.col(2), DELTA);

		Matrix b = Matrix.build(3)
				.row(1, 2, 3)
				.row(4, 5, 6)
				.row(7, 8, 9).get();

		assertArrayEquals(new double[] {1, 4, 7}, b.col(0), DELTA);
		assertArrayEquals(new double[] {2, 5, 8}, b.col(1), DELTA);
		assertArrayEquals(new double[] {3, 6, 9}, b.col(2), DELTA);
	}

	@Test
	public void testColSet() throws Exception {
		Matrix m = Matrix.identity(3).col(1, 2, 3, 4);

		assertArrayEquals(new double[] {2, 3, 4}, m.col(1), DELTA);
		assertArrayEquals(new double[] {1, 2, 0}, m.row(0), DELTA);
		assertArrayEquals(new double[] {0, 3, 0}, m.row(1), DELTA);
		assertArrayEquals(new double[] {0, 4, 1}, m.row(2), DELTA);
	}

	@Test
	public void testVal() throws Exception {
		Matrix m = Matrix.build(2, 2).row(1, 2).row(3, 4).get();
		assertThat(m.val(0, 0), is(equalTo(1.0)));
		assertThat(m.val(0, 1), is(equalTo(2.0)));
		assertThat(m.val(1, 0), is(equalTo(3.0)));
		assertThat(m.val(1, 1), is(equalTo(4.0)));
	}

	@Test
	public void testAdd() throws Exception {
		assertArrayEquals(
				Matrix.identity(2).scale(2).data(),
				Matrix.identity(2).add(Matrix.identity(2)).data(),
				DELTA);

		assertArrayEquals(
				Matrix.zeroes(2).data(),
				Matrix.identity(2).add(Matrix.identity(2).negate()).data(),
				DELTA);
	}

	@Test
	public void testScale() throws Exception {
		assertThat(Matrix.identity(2).scale(2).boxData(), is(array(
				equalTo(2.0), equalTo(0.0),
				equalTo(0.0), equalTo(2.0))));

		assertThat(Matrix.identity(2).scale(0).boxData(), is(array(
				equalTo(0.0), equalTo(0.0),
				equalTo(0.0), equalTo(0.0))));
	}

	@Test
	public void testMultiply() throws Exception {
		Matrix a = Matrix.identity(3);
		Matrix b = Matrix.build(3)
				.row(1, 2, 3)
				.row(4, 5, 6)
				.row(7, 8, 9).get();
		Matrix c = Matrix.build(2, 3)
				.row(5, 4, 3)
				.row(2, 1, 0).get();

		assertTrue(b.multiply(a).epsilonEquals(b));
		assertTrue(b.multiply(b).epsilonEquals(Matrix.build(3)
				.row(30, 36, 42)
				.row(66, 81, 96)
				.row(102, 126, 150).get()));

		assertTrue(b.multiply(c).epsilonEquals(Matrix.build(2, 3)
				.row(42, 54, 66)
				.row(6, 9, 12).get()));
	}

	@Test
	public void testTranspose() throws Exception {
		Matrix m = Matrix.build(2, 3).row(1, 2, 3).row(4, 5, 6).get();
		Matrix t = m.transpose();

		assertThat(t.rows(), is(equalTo(m.cols())));
		assertThat(t.cols(), is(equalTo(m.rows())));

		assertArrayEquals(m.row(0), t.col(0), DELTA);
		assertArrayEquals(m.row(1), t.col(1), DELTA);

		assertArrayEquals(
				Matrix.identity(2).data(),
				Matrix.identity(2).transpose().data(),
				DELTA);
	}

	@Test
	public void testEpsilonEquals() throws Exception {
		Matrix a = Matrix.identity(3);
		Matrix b = Matrix.zeroes(3);

		assertThat(a.epsilonEquals(b), is(false));
		assertThat(a.epsilonEquals(a), is(true));
	}

	@Test
	public void testBuild() throws Exception {
		Matrix m = Matrix.build(2, 2)
				.row(1, 2)
				.row(3, 4)
				.get();

		assertThat(m.boxData(), is(array(
				equalTo(1.0), equalTo(2.0),
				equalTo(3.0), equalTo(4.0))));
	}

	@Test
	public void testIdentity() throws Exception {
		assertThat(Matrix.identity(0).boxData(), is(emptyArray()));

		assertThat(Matrix.identity(1).boxData(), is(array(equalTo(1.0))));

		assertThat(Matrix.identity(2).boxData(), is(array(
				equalTo(1.0), equalTo(0.0),
				equalTo(0.0), equalTo(1.0))));
	}
}