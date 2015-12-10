package org.timothyb89.trace.math;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author timothyb
 */
public class Matrix {

	public static final double EPSILON = 0.000001d;

	private final int rows;
	private final int cols;

	protected double[] data;

	public Matrix(int rows, int cols) {
		if (rows <= 0 || cols <= 0) {
			throw new IllegalArgumentException("Invalid matrix dimensions!");
		}

		this.rows = rows;
		this.cols = cols;

		data = new double[rows * cols];
	}

	public Matrix(int rows, int cols, double[] data) {
		if (rows <= 0 || cols <= 0) {
			throw new IllegalArgumentException("Invalid matrix dimensions!");
		}

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

	public Matrix data(double[] data) {
		if (data.length != rows * cols) {
			throw new IllegalArgumentException("Unable to change data length");
		}

		this.data = Arrays.copyOf(data, rows * cols);

		return this;
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

	public Vector vectorCol(int col) {
		return Vector.of(col(col));
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

	public Matrix col(int col, Vector vector) {
		return col(col, vector.data());
	}

	public Matrix colInsert(int col, double... values) {
		if (col < 0 || col >= cols) {
			throw new IllegalArgumentException(
					"Column is out of bounds: " + col);
		}

		if (values.length > rows) {
			throw new IllegalArgumentException(
					"Too many values to insert into column");
		}

		for (int i = 0; i < values.length; i++) {
			val(i, col, values[i]);
		}

		return this;
	}

	public Matrix colInsert(int col, Vector vector) {
		return colInsert(col, vector.data());
	}

	public Stream<double[]> colStream() {
		return IntStream.range(0, cols).mapToObj(i -> col(i));
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

	public Matrix row(int row, Vector vector) {
		return row(row, vector.data());
	}

	public Matrix rowInsert(int row, double... values) {
		if (row < 0 || row >= rows) {
			throw new IllegalArgumentException("Row is out of bounds: " + row);
		}

		System.arraycopy(values, 0, data, cols * row, values.length);

		return this;
	}

	public Matrix rowInsert(int row, Vector vector) {
		return rowInsert(row, vector.data());
	}

	/**
	 * Add each element of the given matrix to this matrix, storing the result
	 * back in this matrix.
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

	/**
	 * Subtract each element of the given matrix from this matrix, storing the
	 * result back in this matrix.
	 * @param other the matrix to subtract from this matrix
	 * @return this matrix
	 */
	public Matrix sub(Matrix other) {
		if (rows != other.rows || cols != other.cols) {
			throw new IllegalArgumentException("Matrix size mismatch");
		}

		for (int i = 0; i < rows * cols; i++) {
			data[i] -= other.data[i];
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
	 * Multiply the specified matrix "into" this matrix and return the result as
	 * a new matrix, such that {@code ret = [other] * [this]}.
	 *
	 * @param other the matrix to multiply by
	 * @return the result of the multiplication in a new matrix
	 */
	public Matrix multiply(Matrix other) {
		if (other.cols != this.rows) {
			throw new IllegalArgumentException("Dimension mismatch");
		}

		return multiplyIterative(other);
	}

	/**
	 * Generate a minor of this matrix, excluding row {@code i} and column
	 * {@code j} to produce a matrix of size {@code rows - 1 x cols - 1}.
	 * @param i the row index to exclude
	 * @param j the column index to exclude
	 * @return the minor of this matrix at the given row and column
	 */
	public Matrix minor(int i, int j) {
		double[] ret = new double[(rows - 1) * (cols - 1)];

		int retIndex = 0;
		for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {
			if (dataIndex / cols == i) {
				continue;
			}

			if (dataIndex % cols == j) {
				continue;
			}

			ret[retIndex] = data[dataIndex];
			retIndex++;
		}

		return new Matrix(rows - 1, cols - 1, ret);

	}

	/**
	 * Calculates the determinant of this matrix.
	 *
	 * @return the calculated determinant
	 */
	public double det() {
		if (rows != cols) {
			throw new IllegalArgumentException(
					"Cannot calculate determinant of non-square matrix!");
		}

		int size = rows;
		if (size == 1) {
			return data[0];
		} else if (size == 2) {
			return (data[0] * data[3]) - (data[1] * data[2]);
		} else {
			double det = 0;

			// move along first row.
			for (int i = 0; i < size; i++) {
				if (i % 2 == 0) {
					det += data[i] * minor(0, i).det();
				} else {
					det -= data[i] * minor(0, i).det();
				}
			}

			return det;
		}
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

	public static Matrix of(Vector... vectors) {
		Matrix ret = new Matrix(vectors[0].length(), vectors.length);

		for (int i = 0; i < vectors.length; i++) {
			ret.col(i, vectors[i].data());
		}

		return ret;
	}

}
