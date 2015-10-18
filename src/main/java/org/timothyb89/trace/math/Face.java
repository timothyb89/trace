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
@ToString(of = {"vertices", "surfaceNormal"})
public class Face {
	
	private final Model parent;
	private final int[] vertices;

	private final Vector surfaceNormal;
	
	public Face(Model parent, int... vertices) {
		this.parent = parent;
		this.vertices = vertices;
		
		surfaceNormal = calcNormal();
	}
	
	public Face(Model parent, List<Integer> vertices) {
		this.parent = parent;
		this.vertices = vertices.stream().mapToInt(i -> i).toArray();
		
		surfaceNormal = calcNormal();
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

	public Vector ixPoint(Vector l, Vector unit) {
		// N*P = -d, P = any vertex on face
		double d = -1 * surfaceNormal.dot(firstVertex3());

		double nL = surfaceNormal.dot(l);
		double nU = surfaceNormal.dot(unit);
		if (nU == 0) {
			return null;
		}

		// t = -(N*L + d) / N*U
		double t = -(nL + d) / nU;
		if (t <= 0) {
			return null;
		}

		// TODO: ix is only relevant if t is > 0
		// otherwise behind camera

		// P = L + tU
		return l.copy().add(unit.copy().scale(t));
	}

	public boolean intersects(Vector l, Vector unit) {
		// assuming polys from PLY model are in order?
		Vector p = ixPoint(l, unit);
		if (p == null) {
			return false;
		}

		Vector[] edges = edgeStream()
				.map(v -> v.trim(3))
				.toArray(Vector[]::new);

		// N = e1 x e2
		Vector n = edges[0].copy().cross(edges[1]);

		for (int i = 0; i < size(); i++) {
			Vector v = vertex(i).trim(3);
			Vector e = edges[i];

			Vector epvj = p.copy().sub(v);
			Vector np = epvj.cross(e);

			// round-off check? ~0
			double res = np.dot(n);
			if (res < 0) {
				// TODO: is this valid?
				return false;
			}
		}

		return true;
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
	
	public Vector firstVertex() {
		return parent.vertex(vertices[0]);
	}
	
	public Vector firstVertex3() {
		return firstVertex().trim(3);
	}
	
	public int size() {
		return vertices.length;
	}
	
}
