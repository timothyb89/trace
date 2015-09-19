package org.timothyb89.trace.math;

import lombok.experimental.Accessors;

import java.util.Arrays;

/**
 * @author timothyb
 */
@Accessors(fluent = true)
public class Vector extends Matrix {

	public Vector(int length) {
		super(length, 1);
	}

	public Vector(double[] data) {
		super(data.length, 1, Arrays.copyOf(data, data.length));
	}

	public Vector(Vector other) {
		super(other.data.length, 1,
				Arrays.copyOf(other.data, other.data.length));
	}

	public double val(int index) {
		return data[index];
	}

	public Vector val(int index, double value) {
		data[index] = value;

		return this;
	}

	public Vector data(double[] data) {
		this.data = data;

		return this;
	}

	public double[] col(int i) {
		if (i != 0) {
			throw new IllegalArgumentException("Vectors only have 1 column");
		}

		return data();
	}

	public Vector col(int i, double[] data) {
		if (i != 0) {
			throw new IllegalArgumentException("Vectors only have 1 column");
		}

		return data(data);
	}

	public int length() {
		return data.length;
	}

	public Matrix concat(Vector... other) {
		Matrix ret = new Matrix(data.length, other.length);
		ret.col(0, data);

		for (int i = 0; i < other.length; i++) {
			ret.col(i + 1, other[i].data);
		}

		return this;
	}

	/**
	 * Determine the dot product of this vector with the provided vector,
	 * storing the result back into this vector. This method explicitly allows
	 *
	 * @param other the vector to dot with
	 * @return this vector
	 */
	public Vector dot(Vector other) {
		if (other.data.length != data.length) {
			throw new IllegalArgumentException(
					"Unable to dot: dimension mismatch");
		}

		for (int i = 0; i < data.length; i++) {
			data[i] *= other.data[i];
		}

		return this;
	}

	public Vector copy() {
		return new Vector(data);
	}

	public static Vector of(double... values) {
		return new Vector(values);
	}

}
