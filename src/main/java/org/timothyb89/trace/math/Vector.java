package org.timothyb89.trace.math;

import lombok.experimental.Accessors;

import java.util.Arrays;
import java.util.stream.DoubleStream;

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

	@Override
	public Vector data(double[] data) {
		this.data = data;

		return this;
	}

	@Override
	public double[] col(int i) {
		if (i != 0) {
			throw new IllegalArgumentException("Vectors only have 1 column");
		}

		return data();
	}

	@Override
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
	 * returning the result of the operation {@code other * this}.
	 *
	 * @param other the vector to dot with
	 * @return this computed dot product
	 */
	public double dot(Vector other) {
		if (other.data.length != data.length) {
			throw new IllegalArgumentException(
					"Unable to dot: dimension mismatch");
		}

		double ret = 0;
		
		for (int i = 0; i < data.length; i++) {
			ret += data[i] * other.data[i];
		}

		return ret;
	}
	
	/**
	 * Determine the cross product as {@code other x this}, returning a new
	 * vector containing the cross product of {@code other} as if it were
	 * crossed "into" this vector.
	 * 
	 * <p>Note that the cross product is only defined for 3-long vectors.</p>
	 * @param other this vector to cross "into" this vector
	 * @return the result of the cross product as a new vector
	 */
	public Vector cross(Vector other) {
		if (data.length != 3 || other.data.length != 3) {
			throw new IllegalArgumentException("Vector size mismatch");
		}
		
		return Vector.of(
				(other.data[1] * this.data[2]) - (other.data[2] * this.data[1]),
				(other.data[2] * this.data[0]) - (other.data[0] * this.data[2]),
				(other.data[0] * this.data[1]) - (other.data[1] * this.data[0]));
	}
	
	/**
	 * Calculates the norm of this vector, storing the result back into this
	 * vector.
	 * @return this vector
	 */
	public Vector normalize() {
		double length = Math.sqrt(DoubleStream.of(data).map(x -> x * x).sum());
		if (length == 0) {
			return this;
		}
		
		for (int i = 0; i < data.length; i++) {
			data[i] /= length;
		}
		
		return this;
	}
	
	@Override
	public Vector scale(double factor) {
		super.scale(factor);
		
		return this;
	}

	public boolean orthogonal(Vector other) {
		double d = other.dot(this);
		
		return d > -EPSILON && d < EPSILON;
	}
	
	@Override
	public Vector copy() {
		return new Vector(data);
	}

	public static Vector of(double... values) {
		return new Vector(values);
	}
	
	public static Vector zeroes(int length) {
		return new Vector(new double[length]);
	}

}
