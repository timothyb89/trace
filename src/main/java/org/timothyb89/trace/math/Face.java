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
		//double d = -1 * surfaceNormal.dot(firstVertex3());
		double d = -1 * surfaceNormal.dot(vertex3(2));

		//System.out.println("d=" + d);
		
		double nL = surfaceNormal.dot(l);
		double nU = surfaceNormal.dot(unit);
		if (nU == 0) {
			//System.err.println("error: parallel");
			return null;
		}

		//System.out.println("n*l=" + nL);
		//System.out.println("n*u=" + nU);
		
		// t = -(N*L + d) / N*U
		double t = ((-d) - nL) / nU;
		if (t <= 0) {
			//System.out.println("error: behind camera: " + t);
			return null;
		}

		// TODO: ix is only relevant if t is > 0
		// otherwise behind camera

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

		Side lastSide = null;
		for (int i = 0; i < size(); i++) {
			Vector v = vertex3(i);
			Vector e = edges[i];

			Vector epvj = p.copy().sub(v);
			Vector np = epvj.cross(e);

			double res = np.dot(n);
			
			Side side = getSide(res);
			//System.out.println("res: " + res + ", side: " + side);
			if (lastSide == null) {
				lastSide = side;
			} else if (side != lastSide) {
				//System.out.println("violated! :(");
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
	
	public Vector vertex3(int faceIndex) {
		return parent.vertex(vertices[faceIndex]).trim(3);
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
	
	private enum Side {
		LEFT,
		RIGHT,
		ON
	}
	
}
