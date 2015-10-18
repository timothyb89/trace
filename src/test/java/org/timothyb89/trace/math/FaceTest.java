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
	private Face b;
	private Face c;
	private Face d;

	@Before
	public void setUp() {
		camera = CameraParser.readPath(Paths.get("data/unitx.cam")).camera();
		model = PLYParser.readPath(Paths.get("data/unitplane.ply")).toModel();
		a = model.face(0);
	}

	@Test
	public void testIxPoint() throws Exception {
		System.out.println("plane: " + model.centerMass());

		Vector l = camera.lensPoint(0, 0);
		System.out.println("lens point: " + l);

		Vector e = camera.focalPoint();
		System.out.println("focal point: " + e);
		Vector u = l.copy().sub(e).normalize();
		System.out.println("u: " + u);
		Vector ix = a.ixPoint(l, u);
		System.out.println("ix: " + ix);
	}

	@Test
	public void testSurfaceNormal() throws Exception {
		System.out.println(a);
	}

}