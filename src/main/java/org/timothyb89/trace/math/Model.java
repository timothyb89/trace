package org.timothyb89.trace.math;

/**
 * @author timothyb
 */
public class Model {

	private Matrix vertexData;
	
	
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
