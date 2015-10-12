package org.timothyb89.trace.math;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author tim
 */
@Data
@Accessors(fluent = true)
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
		
		return c.sub(b)
				.cross(b.sub(a))
				.normalize();
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
		return IntStream.of(vertices).mapToObj(i -> parent.vertex(i));
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
