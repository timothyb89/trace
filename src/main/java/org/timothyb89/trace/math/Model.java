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

	private double min(int i, double current, double candidate) {
		if (i == 0 || candidate < current) {
			return candidate;
		}

		return current;
	}

	private double max(int i, double current, double candidate) {
		if (i == 0 || candidate > current) {
			return candidate;
		}

		return current;
	}

	public Matrix boundingBox() {
		double minX = 0;
		double minY = 0;
		double minZ = 0;

		double maxX = 0;
		double maxY = 0;
		double maxZ = 0;

		for (int i = 0; i < vertexData.cols(); i++) {
			double[] col = vertexData.col(i);

			minX = min(i, minX, col[0]);
			minY = min(i, minY, col[1]);
			minZ = min(i, minZ, col[2]);

			maxX = max(i, maxX, col[0]);
			maxY = max(i, maxY, col[1]);
			maxZ = max(i, maxZ, col[2]);
		}

		return Matrix.build(3, 2)
				.row(minX, maxX)
				.row(minY, maxY)
				.row(minZ, maxZ).get();
	}
	
	public Model transform(Matrix matrix) {
		vertexData = vertexData.multiply(matrix);
		return this;
	}

}
