package org.timothyb89.trace.math;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author timothyb
 */
@Data
@Accessors(fluent = true)
public class PointLight {

	private Vector position;

	private double red;
	private double green;
	private double blue;
	private double alpha = 1;

	public PointLight(Vector color, Vector position) {
		color(color);
		position(position);
	}

	public PointLight color(Vector color) {
		System.out.println("color: " + color + ", length " + color.length());
		if (color.length() != 3 && color.length() != 4) {
			throw new IllegalArgumentException(
					"Color must have 3 or 4 parameters");
		}

		red = color.val(0);
		green = color.val(1);
		blue = color.val(2);

		if (color.length() == 4) {
			alpha = color.val(3);
		}

		return this;
	}

	public PointLight color(double red, double green, double blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;

		return this;
	}

	public PointLight color(double red, double green, double blue, double alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;

		return this;
	}

	public Vector rgb() {
		return Vector.of(red, green, blue);
	}

	public boolean isAmbient() {
		return position == null;
	}

}
