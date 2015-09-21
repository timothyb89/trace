package org.timothyb89.trace.math;

import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author timothyb
 */
@Accessors(fluent = true)
public class Model {

	@Getter private Matrix vertexData;
	@Getter private List<List<Integer>> faces;

	public Model(Matrix vertexData, List<List<Integer>> faces) {
		this.vertexData = vertexData;
		this.faces = faces;
	}

	public int countVertices() {
		return vertexData.cols();
	}
	
	public Vector centerMass() {
		double sumX = 0;
		double sumY = 0;
		double sumZ = 0;
		
		for (int i = 0; i < vertexData.cols(); i++) {
			double[] col = vertexData.col(i);
			
			sumX += col[0];
			sumY += col[1];
			sumZ += col[2];
		}
		
		return Vector.of(
				sumX / vertexData.cols(),
				sumY / vertexData.cols(),
				sumZ / vertexData.cols());
	}
	
	public void transform(Matrix matrix) {
		vertexData = vertexData.multiply(matrix);
	}

}
