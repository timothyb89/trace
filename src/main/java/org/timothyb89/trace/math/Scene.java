package org.timothyb89.trace.math;

import java.util.List;
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
	
}
