package org.timothyb89.trace.math;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 * @author tim
 */
@Data
@Accessors(fluent = true)
@ToString(of = {"index", "vertices", "surfaceNormal"})
public class Face {

	private final Model parent;
	private final int index;
	private final int[] vertices;
	private final Vector[] edges;

	private Material material;

	private final Vector surfaceNormal;

	public Face(Model parent, int index, int... vertices) {
		this.parent = parent;
		this.index = index;
		this.vertices = vertices;

		material = Material.DEFAULT;

		surfaceNormal = calcNormal();
		edges = calcEdges();
	}

	public Face(Model parent, int index, List<Integer> vertices) {
		this.parent = parent;
		this.index = index;
		this.vertices = vertices.stream().mapToInt(i -> i).toArray();

		material = Material.DEFAULT;

		surfaceNormal = calcNormal();
		edges = calcEdges();
	}

	private Vector calcNormal() {
		// plz don't be colinear, kthx
		// also we'll need >= 3 vertices
		Vector a = parent.vertex(vertices[0]).trim(3);
		Vector b = parent.vertex(vertices[1]).trim(3);
		Vector c = parent.vertex(vertices[2]).trim(3);

		Vector crossA = b.copy().sub(a);
		Vector crossB = c.copy().sub(b);

		return crossB.cross(crossA).normalize();
	}

	private Vector[] calcEdges() {
		return edgeStream()
				.map(v -> v.trim(3))
				.toArray(Vector[]::new);
	}

	public double ixDistance(Vector point, Vector direction) {
		// N*P = -d, P = any vertex on face
		//double d = -1 * surfaceNormal.dot(firstVertex3());
		double d = -1 * surfaceNormal.dot(vertex3(2));

		double nL = surfaceNormal.dot(point);
		double nU = surfaceNormal.dot(direction);
		if (nU == 0) {
			// parallel
			return 0;
		}

		// t = -(N*L + d) / N*U
		return ((-d) - nL) / nU;
	}

	public Vector ixPoint(Vector point, Vector direction, double t) {
		return point.copy().add(direction.copy().scale(t));
	}

	public Vector ixPoint(Vector l, Vector unit) {
		// N*P = -d, P = any vertex on face
		//double d = -1 * surfaceNormal.dot(firstVertex3());
		double d = -1 * surfaceNormal.dot(vertex3(2));

		double nL = surfaceNormal.dot(l);
		double nU = surfaceNormal.dot(unit);
		if (nU == 0) {
			// parallel
			return null;
		}

		// t = -(N*L + d) / N*U
		double t = ((-d) - nL) / nU;
		if (t <= 0) {
			// behind camera
			return null;
		}

		// P = L + tU
		return l.copy().add(unit.copy().scale(t));
	}

	public Vector ixPointAny(Vector normal, Vector l, Vector unit) {
		// N*P = -d, P = any vertex on face
		double d = -1 * normal.dot(firstVertex3());

		double nL = normal.dot(l);
		double nU = normal.dot(unit);
		if (nU == 0) {
			// parallel
			return null;
		}

		// t = -(N*L + d) / N*U
		double t = ((-d) - nL) / nU;
		if (t >= -Vector.EPSILON && t <= Vector.EPSILON) {
			// bad?
			return null;
		}

		// P = L + tU
		return l.copy().add(unit.copy().scale(t));
	}

	private Side getSide(double value) {
		if (value < -Vector.EPSILON) {
			return Side.LEFT;
		} else if (value >= Vector.EPSILON) {
			return Side.RIGHT;
		} else {
			return Side.LEFT;
		}
	}

	public boolean intersects(Vector ix) {
		if (ix == null) {
			return false;
		}

		// N = e1 x e2
		Vector n = edges[0].copy().cross(edges[1]);

		Side lastSide = null;
		for (int i = 0; i < size(); i++) {
			Vector v = vertex3(i);
			Vector e = edges[i];

			Vector epvj = ix.copy().sub(v);
			Vector np = epvj.cross(e);

			double res = np.dot(n);

			Side side = getSide(res);
			if (lastSide == null) {
				lastSide = side;
			} else if (side != lastSide) {
				// not on same side, not in polygon
				return false;
			}
		}

		return true;
	}

	public boolean intersects(Vector point, Vector direction) {
		return intersects(ixPoint(point, direction));
	}

	public boolean contains(int vertex) {
		for (int v : vertices) {
			if (v == vertex) {
				return true;
			}
		}

		return false;
	}

	public Stream<Vector> vertexStream() {
		return IntStream.of(vertices).mapToObj(parent::vertex);
	}

	public Stream<Vector> edgeStream() {
		return IntStream.range(0, vertices.length)
				.mapToObj(i -> {
					Vector a = vertex(i);
					Vector b = vertex((i + 1) % vertices.length);
					return b.sub(a);
				});

	}

	public Vector vertex(int faceIndex) {
		return parent.vertex(vertices[faceIndex]);
	}

	public Vector vertex3(int faceIndex) {
		return parent.vertex(vertices[faceIndex]).trim(3);
	}

	public Vector firstVertex() {
		return parent.vertex(vertices[0]);
	}

	public Vector firstVertex3() {
		return firstVertex().trim(3);
	}

	public Vector midpoint() {
		Vector sum = Vector.zeroes(4);

		vertexStream().forEach(sum::add);

		return Vector.of(
				sum.val(0) / vertices.length,
				sum.val(1) / vertices.length,
				sum.val(2) / vertices.length);
	}

	public int size() {
		return vertices.length;
	}

	private enum Side {
		LEFT,
		RIGHT;
	}

}
