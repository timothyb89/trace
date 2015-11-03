package org.timothyb89.trace.math;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author timothyb
 */
@Data
@Accessors(fluent = true)
public class Material {

	public static final Material DEFAULT = Material.of(
			Vector.of(0.5, 0.5, 0.5),
			0, 1);

	private Matrix diffuse;
	private double specularity;
	private double shininess;

	public static Material of(
			Vector color,
			double specularity, double shininess) {
		Material mat = new Material();
		mat.diffuse(Matrix.build(3)
				.row(color.val(0), 0, 0)
				.row(0, color.val(1), 0)
				.row(0, 0, color.val(2)).get());
		mat.specularity(specularity);
		mat.shininess(shininess);

		return mat;
	}

}
