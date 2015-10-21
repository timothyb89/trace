package org.timothyb89.trace.math;

import org.junit.Before;
import org.junit.Test;
import org.timothyb89.trace.model.camera.CameraParser;
import org.timothyb89.trace.model.ply.PLYParser;

import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * @author timothyb
 */
public class FaceTest {

	private Camera camera;
	private Model model;
	private Face a;

	private Camera sphereCamera;
	private Model sphere;

	@Before
	public void setUp() {
		camera = CameraParser.readPath(Paths.get("data/unitx.cam")).camera();
		model = PLYParser.readPath(Paths.get("data/unitplane.ply")).toModel();
		a = model.face(0);

		sphereCamera = CameraParser.readPath(Paths.get("data/sphere.cam")).camera();
		sphere = PLYParser.readPath(Paths.get("data/sphere.ply")).toModel();
	}

	@Test
	public void testIxPoint() throws Exception {
		Vector l = camera.lensPoint(0, 0);
		Vector e = camera.focalPoint();
		Vector u = l.copy().sub(e).normalize();
		
		Vector ix = a.ixPoint(l, u);
		System.out.println(ix);
		assertTrue(ix != null);
		assertTrue(ix.epsilonEquals(Vector.of(0, 0, 0)));
	}

	@Test
	public void testIntersects() throws Exception {
		Vector l = camera.lensPoint(0, 0);
		Vector e = camera.focalPoint();
		Vector u = l.copy().sub(e).normalize();
		assertTrue(a.intersects(l, u));
		
		l = camera.lensPoint(1, 0);
		e = camera.focalPoint();
		u = l.copy().sub(e).normalize();
		assertFalse(a.intersects(l, u));
	}

	@Test
	public void testRounding() throws Exception {
		Vector l = sphereCamera.lensPoint(0, 0);
		Vector e = sphereCamera.focalPoint();
		Vector u = l.copy().sub(e).normalize();

		Face ix = sphere.intersect(l, u);
		System.out.println(ix);
		//assertTrue(ix != null);
	}

}