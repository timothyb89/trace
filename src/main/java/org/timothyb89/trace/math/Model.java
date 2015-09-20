package org.timothyb89.trace.math;

import java.util.List;
import lombok.experimental.Accessors;

/**
 * @author timothyb
 */
@Accessors(fluent = true)
public class Model {

	private Matrix vertexData;
	private List<List<Integer>> faces;

	public Model(Matrix vertexData, List<List<Integer>> faces) {
		this.vertexData = vertexData;
		this.faces = faces;
	}
	
	public Matrix vertexData() {
		return vertexData;
	}
	
	public Model vertexData(Matrix vertexData) {
		this.vertexData = vertexData;
		
		return this;
	}

	public int countVertices() {
		return vertexData.cols();
	}
	
	public Vector centerMass() {
		throw new UnsupportedOperationException("TODO");
	}
	
	/**
	 * Rotate the vertices in this model {@code theta} radians about the axis
	 * defined by the vector {@code axis}.
	 * @param axis
	 * @param theta
	 * @return 
	 */
	public Model rotate(Vector axis, double theta) {
		throw new UnsupportedOperationException("TODO");
	}

}
