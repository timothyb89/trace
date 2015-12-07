package org.timothyb89.trace.math;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author timothyb
 */
@Data
@Accessors(fluent = true)
public class Scene {

	private Camera camera;
	private List<Model> models;

	private PointLight ambientLight;
	private List<PointLight> lights;

	public Scene(Camera camera, List<Model> models) {
		this.camera = camera;
		this.models = models;

		lights = new ArrayList<>();
	}

	public Scene addLight(PointLight light) {
		lights.add(light);

		return this;
	}

	public Model model(int index) {
		return models.get(index);
	}

	public Stream<Face> faces() {
		return models.stream().flatMap(model -> model.faces().stream());
	}

}
