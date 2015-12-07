package org.timothyb89.trace.math.tracer;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.timothyb89.trace.math.*;
import org.timothyb89.trace.util.Tuple;

/**
 * A trace task for a particular row and column.
 * @author timothyb
 */
public class TraceTask implements Callable<TraceResult> {

	private static final int AA_ITERATIONS = 0;

	public static final int MAX_DEPTH = 10;

	public static final double MIN_RECURSE_INTENSITY = 1.0 / 512.0;

	private final CountDownLatch latch;
	private final Scene scene;
	private final int row;
	private final int col;

	public TraceTask(CountDownLatch latch, Scene scene, int row, int col) {
		this.latch = latch;
		this.scene = scene;
		this.row = row;
		this.col = col;
	}

	private Tuple<Face, Vector> intersect_(Vector point, Vector direction) {
		// tries to use min t values, but causes self occlusion in shaded() :(
		double minT = 0;
		Vector minIx = null;
		Face minFace = null;

		for (Model m : scene.models()) {
			for (Face f : m.faces()) {
				double t = f.ixDistance(point, direction);

				// must be in front of camera
				if (t <= 0) {
					continue;
				}

				if (minIx == null || t < minT) {
					Vector ix = f.ixPoint(point, direction, t);

					if (f.intersects(ix)) {
						minT = t;
						minFace = f;
						minIx = ix;
					}
				}
			}
		}

		if (minIx == null) {
			return null;
		} else {
			return new Tuple<>(minFace, minIx);
		}
	}

	private Tuple<Face, Vector> intersect(Vector point, Vector direction) {
		for (Model m : scene.models()) {
			for (Face f : m.faces()) {
				double t = f.ixDistance(point, direction);

				// must be in front of camera
				if (t <= 0) {
					continue;
				}

				Vector ix = f.ixPoint(point, direction, t);

				if (f.intersects(ix)) {
					return new Tuple<>(f, ix);
				}
			}
		}

		return null;
	}

	private boolean shaded(Vector point, Face face, PointLight light, Vector n) {
		// find ray from ix point -> light source (direction vector)
		Vector l = light.position().copy().sub(point);
		double distL = l.distance();
		l.normalize();

		//System.out.printf("%2d, %2d, #%4d: n=%s, l=%s\n", row, col, face.index(), n, l);

		// check for self-occlusion
		double nL = n.dot(l);
		if (nL < 0) { // TODO: round-off error?
			//System.out.println("self occluded :(");
			return true;
		}

		// make sure the path is clear
		for (Model m : scene.models()) {
			for (Face f : m.faces()) {
				if (f == face) {
					// don't attempt ix with the current face
					continue;
				}

				double t = f.ixDistance(point, l);
				if (t == 0) {
					continue;
				}

				// make sure poly f falls between the ix face and the light
				// source
				if (t < Vector.EPSILON || t > distL) {
					continue;
				}

				Vector subIx = f.ixPoint(point, l, t);
				if (f.intersects(subIx)) {
					// occluded, not visible
					return true;
				}
			}
		}

		return false;
	}

	private Vector ambient(Face face) {
		Matrix diffuseMatrix = face.material().diffuse();
		Vector ambientColor; // Ba
		if (scene.ambientLight() != null) {
			ambientColor = scene.ambientLight().rgb();
		} else {
			ambientColor = Vector.of(0, 0, 0); // TODO: ?
		}

		// I = Kd * Ba
		return ambientColor.multiply(diffuseMatrix);
	}

	private Vector diffuse(Face face, PointLight light, Vector n, Vector l) {
		return light.rgb()
				.multiply(face.material().diffuse())
				.scale(n.dot(l));
	}

	private Vector specular(
			Face face, PointLight light,
			Vector n, Vector l, Vector v) {
		// determine reflected ray
		Vector r = n.copy()
				.scale(2 * n.dot(l))
				.sub(l);

		// specular intensity
		// don't allow negative intensities - ???
		return light.rgb()
				.scale(face.material().specularity())
				.scale(Math.pow(v.dot(r), face.material().shininess()));
	}

	private Vector reflect(Vector point, Vector direction, int depth, double ksp) {
		Vector intensity = Vector.zeroes(3);
		if (depth > MAX_DEPTH) {
			return intensity;
		}

		// find the world intersection details - only 1 can exist
		Tuple<Face, Vector> ixTuple = intersect(point, direction);
		if (ixTuple == null) {
			return intensity; // TODO should this be ambient at least?
		}

		Face face = ixTuple.a();
		Vector ix = ixTuple.b();

		// find opposite view vector and the correct surface normal
		// if negative, flip it
		Vector v = direction.copy().scale(-1);
		Vector n = face.surfaceNormal();
		if (n.dot(v) < 0) { // TODO: check rounding error?
			n = n.copy().scale(-1);
		}

		intensity.add(ambient(face));

		for (PointLight light : scene.lights()) {
			if (!shaded(ix, face, light, n)) {
				// find ray from ix point -> light source (direction vector)
				Vector l = light.position().copy().sub(ix);
				l.normalize();

				intensity.add(diffuse(face, light, n, l));
				intensity.add(specular(face, light, n, l, v));
			}
		}

		ksp *= face.material().specularity();
		if (ksp > MIN_RECURSE_INTENSITY) {
			// bounce around more
			Vector reflected = n.copy()
					.scale(2 * n.dot(v))
					.sub(v);

			intensity.add(reflect(ix, reflected, depth + 1, ksp));
		}

		return intensity;
	}

	//@Override
	public TraceResult call() throws Exception {
		Camera camera = scene.camera();

		if (AA_ITERATIONS > 0) {
			double sumR = 0;
			double sumG = 0;
			double sumB = 0;

			Random rand = new Random();
			for (int i = 0; i < AA_ITERATIONS; i++) {
				// find ray from focal point -> camera point for this row,col
				// (direction vector)
				Vector l = camera.lensPoint(
						((double) row) - 0.5 + rand.nextDouble(),
						((double) col) - 0.5 + rand.nextDouble());
				Vector e = camera.focalPoint();
				Vector unit = l.copy().sub(e).normalize();
				Vector color = reflect(l, unit, 0, 1);

				sumR += color.val(0);
				sumG += color.val(1);
				sumB += color.val(2);
			}

			latch.countDown();
			return new TraceResult(row, col,
					sumR / AA_ITERATIONS,
					sumG / AA_ITERATIONS,
					sumB / AA_ITERATIONS);
		} else {
			Vector l = camera.lensPoint(row, col);
			Vector e = camera.focalPoint();
			Vector unit = l.copy().sub(e).normalize();
			Vector color = reflect(l, unit, 0, 1);

			latch.countDown();
			return new TraceResult(row, col,
					color.val(0),
					color.val(1),
					color.val(2));
		}

	}

}
