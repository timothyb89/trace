package org.timothyb89.trace.math;

/**
 *
 * @author timothyb
 */
public class Transform {
	
	public static Matrix translate(double x, double y, double z) {
		return Matrix.build(4)
				.row(1, 0, 0, x)
				.row(0, 1, 0, y)
				.row(0, 0, 1, z)
				.row(0, 0, 0, 1).get();
	}
	
	public static Matrix scale(double x, double y, double z) {
		return Matrix.build(4)
				.row(x, 0, 0, 0)
				.row(0, y, 0, 0)
				.row(0, 0, z, 0)
				.row(0, 0, 0, 1).get();
	}
	
	public static Matrix rotateZ(double radians) {
		double sin = Math.sin(radians);
		double cos = Math.cos(radians);
		
		return Matrix.build(4)
				.row(cos, -sin, 0, 0)
				.row(sin,  cos, 0, 0)
				.row(0,    0,   1, 0)
				.row(0,    0,   0, 1).get();
	}

	public static Matrix identity() {
		return Matrix.identity(4);
	}

	public static Matrix axisRotate(Vector axis, double radians) {
		// norm rotation axis
		Vector w = axis.copy().normalize();
		
		// axis m -> not parallel to w
		Vector m = w.copy().val(w.minIndex(), 1.0).normalize();

		Vector u = m.cross(w).normalize();
		Vector v = u.cross(w);
		
		Matrix rotAxis = Matrix.identity(4)
				.rowInsert(0, u)
				.rowInsert(1, v)
				.rowInsert(2, w);
		
		Matrix rotZ = rotateZ(radians);
		
		return rotAxis
				.multiply(rotZ)
				.multiply(rotAxis.transpose());
	}
	
}
