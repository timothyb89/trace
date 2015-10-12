package org.timothyb89.trace.math;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Arrays;
import lombok.Builder;

/**
 * @author timothyb
 */
@Data
@Accessors(fluent = true)
public class Camera {

	private final Vector focalPoint;
	private final Vector lookAtPoint;
	private final Vector viewUp;
	private final double focalLength;
	private final int[] bounds;
	
	/**
	 * The View Plane Normal axis (VPN), looking directly down the camera.
	 */
	private Vector n;
	
	/**
	 * The camera "x" axis.
	 */
	private Vector u;
	
	/**
	 * The camera "y" axis.
	 */
	private Vector v;
	
	/**
	 * The base vector for {@code loc()}, FP + dN.
	 */
	private Vector baseLoc;

	@Builder
	public Camera(
			Vector focalPoint, Vector lookAtPoint, Vector viewUp,
			double focalLength, int[] bounds) {
		this.focalPoint = focalPoint;
		this.lookAtPoint = lookAtPoint;
		this.viewUp = viewUp;
		
		this.focalLength = focalLength;
		this.bounds = bounds;
		
		resolveAxes();
	}
	
	private void resolveAxes() {
		n = lookAtPoint.copy().sub(focalPoint).normalize();
		u = n.copy().cross(viewUp).normalize();
		v = n.copy().cross(u);
		
		baseLoc = focalPoint.copy().add(n.copy().scale(focalLength));
	}
	
	public Vector lensPoint(int row, int col) {
		return baseLoc.copy()              // (F + dN)
				.add(u.copy().scale(row))  // + rU
				.add(v.copy().scale(col)); // + cV
	}

	public int[] bounds() {
		return bounds;
	}
	
}
