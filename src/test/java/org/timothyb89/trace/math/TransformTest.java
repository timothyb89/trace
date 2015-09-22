package org.timothyb89.trace.math;

import org.junit.Before;
import org.junit.Test;
import org.timothyb89.trace.model.ply.PLYParser;

import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author timothyb
 */
public class TransformTest {

	private Model model;
	private Vector center;

	@Before
	public void setUp() {
		model = PLYParser.readPath(Paths.get("data/octahedron.ply")).toModel();
		center = model.centerMass();
	}

	@Test
	public void testTranslate() throws Exception {
		assertTrue(center.epsilonEquals(Vector.of(0, 0, 0)));
		model.transform(Transform.translate(1, 2, -3));
		assertTrue(model.centerMass().epsilonEquals(Vector.of(1, 2, -3)));
	}

	@Test
	public void testScale() throws Exception {
		assertTrue(center.epsilonEquals(Vector.of(0, 0, 0)));
		model.transform(Transform.scale(1, 2, 3));
		assertTrue(model.centerMass().epsilonEquals(Vector.of(0, 0, 0)));
	}

	@Test
	public void testAxisRotate() throws Exception {
		Matrix orig = model.vertexData().copy();
		model.transform(Transform.axisRotate(Vector.of(0, 1, 0), 2 * Math.PI));
		assertTrue(model.vertexData().epsilonEquals(orig));
	}
}