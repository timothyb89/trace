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
			0, 1, 0.0);

	private Matrix diffuse;

	/**
	 * Specularity constant k_s
	 */
	private double specularity;

	/**
	 * Phong constant, alpha
	 */
	private double shininess;

	/**
	 * Translucency multiplier, k_t. 0.0 ~= opaque, 1.0 ~= clear.
	 */
	private double translucency;

	public static Material of(
			Vector color,
			double specularity, double shininess, double translucency) {
		Material mat = new Material();
		mat.diffuse(Matrix.build(3)
				.row(color.val(0), 0, 0)
				.row(0, color.val(1), 0)
				.row(0, 0, color.val(2)).get());
		mat.specularity(specularity);
		mat.shininess(shininess);
		mat.translucency(translucency);

		return mat;
	}

	public boolean isOpaque() {
		return translucency == 0.0;
	}

}
