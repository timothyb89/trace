package org.timothyb89.trace.math;

import java.util.Arrays;

/**
 *
 * @author timothyb
 */
public class Matrix {

	public static final double EPSILON = 0.0001d;

	private final int rows;
	private final int cols;

	private double[] data;

	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;

		data = new double[rows * cols];
	}

	public Matrix(int rows, int cols, double[] data) {
		if (rows * cols != data.length) {
			throw new IllegalArgumentException("Invalid data length!");
		}

		this.rows = rows;
		this.cols = cols;
		this.data = data;
	}

	public Matrix(Matrix other) {
		this.rows = other.rows;
		this.cols = other.cols;

		data = Arrays.copyOf(other.data, this.rows * this.cols);
	}

	private Double[] box(double[] array) {
		Double[] ret = new Double[array.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = array[i];
		}

		return ret;
	}

	public double[] data() {
		return data;
	}

	public Double[] boxData() {
		return box(data);
	}

	public int rows() {
		return rows;
	}

	public double[] row(int row) {
		if (row < 0 || row >= rows) {
			throw new IllegalArgumentException("Row is out of bounds: " + row);
		}

		double[] ret = new double[cols];

		System.arraycopy(data, cols * row, ret, 0, cols);

		return ret;
	}

	public Double[] boxRow(int row) {
		return box(row(row));
	}

	public int cols() {
		return cols;
	}

	public double[] col(int col) {
		double[] ret = new double[rows];

		for (int i = 0; i < rows; i++) {
			ret[i] = data[(cols * i) + col];
		}

		return ret;
	}

	public Double[] boxCol(int col) {
		return box(col(col));
	}

	public Matrix col(int col, double... values) {
		if (values.length != rows) {
			throw new IllegalArgumentException(
					"Cannot set column with incorrect number of values.");
		}

		if (col < 0 || col >= cols) {
			throw new IllegalArgumentException("Column out of bounds: " + col);
		}

		for (int i = 0; i < rows; i++) {
			data[(cols * i) + col] = values[i];
		}

		return this;
	}

	public double val(int row, int col) {
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			throw new IllegalArgumentException(
					"Value is out of bounds at " + row + ", " + col);
		}

		return data[(row * cols) + col];
	}

	public Matrix val(int row, int col, double value) {
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			throw new IllegalArgumentException(
					"Value is out of bounds at " + row + ", " + col);
		}

		data[(row * cols) + col] = value;

		return this;
	}

	/**
	 * Set the columns in the specified row to the given values.
	 * @param row the row index to set
	 * @param values the values to set to the row
	 * @return this matrix
	 */
	public Matrix row(int row, double... values) {
		if (values.length != cols) {
			throw new IllegalArgumentException(
					"Cannot set row with incorrect number of values");
		}

		if (row < 0 || row >= rows) {
			throw new IllegalArgumentException("Row is out of bounds: " + row);
		}

		System.arraycopy(values, 0, data, cols * row, cols);

		return this;
	}

	/**
	 * Add each element of the given matrix to this matrix.
	 * @param other the matrix to add into this matrix
	 * @return this matrix
	 */
	public Matrix add(Matrix other) {
		if (rows != other.rows || cols != other.cols) {
			throw new IllegalArgumentException("Matrix size mismatch");
		}

		for (int i = 0; i < rows * cols; i++) {
			data[i] += other.data[i];
		}

		return this;
	}

	public Matrix scale(double factor) {
		for (int i = 0; i < rows * cols; i++) {
			data[i] *= factor;
		}

		return this;
	}

	public Matrix multiplyIterative(Matrix other) {
		Matrix ret = new Matrix(other.rows, this.cols);

		double sum;
		for (int row = 0; row < ret.rows; row++) {
			for (int col = 0; col < ret.cols; col++) {
				sum = 0;

				for (int k = 0; k < other.cols; k++) {
					sum += other.val(row, k) * this.val(k, col);
				}

				ret.val(row, col, sum);
			}
		}

		return ret;
	}

	/**
	 * Multiply the specified matrix "into" this matrix and return the result
	 * as a new matrix, such that {@code ret = [other] * [this]}.
	 * @param other the matrix to multiply by
	 * @return the result of the multiplication in a new matrix
	 */
	public Matrix multiply(Matrix other) {
		if (other.cols != this.rows) {
			throw new IllegalArgumentException("Dimension mismatch");
		}

		return multiplyIterative(other);
	}

	public Matrix power(int pow) {
		// TODO

		return this;
	}

	/**
	 * Transposes this matrix, returning a new matrix with rows and columns
	 * swapped.
	 * @return a new, transposed version of this matrix
	 */
	public Matrix transpose() {
		Matrix ret = new Matrix(cols, rows);
		for (int i = 0; i < rows; i++) {
			ret.col(i, row(i));
		}

		return ret;
	}

	public Matrix negate() {
		return scale(-1);
	}

	public boolean epsilonEquals(Matrix other, double epsilon) {
		if (rows != other.rows || cols != other.cols) {
			return false;
		}

		double v;
		for (int i = 0; i < data.length; i++) {
			v = data[i] - other.data[i];

			if (v > epsilon || v < -epsilon) {
				return false;
			}
		}

		return true;
	}

	public boolean epsilonEquals(Matrix other) {
		return epsilonEquals(other, EPSILON);
	}

	public Matrix copy() {
		return new Matrix(this);
	}

	public String format() {
		StringBuilder sb = new StringBuilder();

		sb.append("[ ");
		for (int row = 0; row < rows; row++) {
			if (row > 0) {
				sb.append("  ");
			}

			for (int col = 0; col < cols; col++) {
				sb.append(String.format("%5.2f ", val(row, col)));
			}

			if (row < rows - 1) {
				sb.append('\n');
			}
		}
		sb.append(']');

		return sb.toString();
	}

	public static class MatrixBuilder {

		private Matrix value;
		private int rowIndex;

		public MatrixBuilder(int rows, int cols) {
			value = new Matrix(rows, cols);
			rowIndex = 0;
		}

		public MatrixBuilder row(double... values) {
			value.row(rowIndex, values);
			rowIndex++;

			return this;
		}

		public Matrix get() {
			return value;
		}

	}

	public static MatrixBuilder build(int rows, int cols) {
		return new MatrixBuilder(rows, cols);
	}

	public static MatrixBuilder build(int size) {
		return new MatrixBuilder(size, size);
	}

	public static Matrix identity(int size) {
		double[] data = new double[size * size];
		for (int i = 0; i < size; i++) {
			data[(size * i) + i] = 1;
		}

		return new Matrix(size, size, data);
	}

	public static Matrix zeroes(int size) {
		return new Matrix(size, size, new double[size * size]);
	}

	public static Matrix zeroes(int rows, int cols) {
		return new Matrix(rows, cols, new double[rows * cols]);
	}

}
