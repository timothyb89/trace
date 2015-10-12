package org.timothyb89.trace.math;

import lombok.Getter;
import lombok.experimental.Accessors;

/**
 *
 * @author timothyb
 */
@Accessors(fluent = true)
public class Image {
	
	@Getter private final int width;
	@Getter private final int height;
	
	@Getter private final int size;
	@Getter private final int[] red;
	@Getter private final int[] green;
	@Getter private final int[] blue;

	public Image(int width, int height) {
		this.width = width;
		this.height = height;
		
		size = width * height;
		
		red = new int[size];
		green = new int[size];
		blue = new int[size];
	}
	
	public Image set(int x, int y, int r, int g, int b) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IllegalArgumentException(
					"Value is out of bounds at " + x + ", " + y);
		}

		int pos = (y * width) + x;
		red[pos] = r;
		green[pos] = g;
		blue[pos] = b;

		return this;
	}
	
}
