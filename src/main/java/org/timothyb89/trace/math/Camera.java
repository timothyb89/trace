package org.timothyb89.trace.math;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;

/**
 * @author timothyb
 */
@Data
@Accessors(fluent = true)
public class Camera {

	private Vector focalPoint;
	private Vector lookAtPoint;
	private Vector viewUp;
	private double focalLength;
	private int[] bounds;

	public int[] bounds() {
		return bounds;
	}

	public Camera bounds(int[] bounds) {
		if (bounds.length != 4) {
			throw new IllegalArgumentException(
					"Invalid bounds: " + Arrays.toString(bounds));
		}

		this.bounds = bounds;

		return this;
	}

}
