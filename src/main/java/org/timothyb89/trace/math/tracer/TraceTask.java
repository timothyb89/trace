package org.timothyb89.trace.math.tracer;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.timothyb89.trace.math.*;

/**
 * A trace task for a particular row and column.
 * @author timothyb
 */
public class TraceTask implements Callable<TraceResult> {

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

	private Vector calculateLighting(Face face, Vector ix, Vector ixDirection) {
		// determine overall ambient lighting
		Matrix diffuseMatrix = face.material().diffuse();
		Vector ambientColor; // Ba
		if (scene.ambientLight() != null) {
			ambientColor = scene.ambientLight().rgb();
		} else {
			ambientColor = Vector.of(0, 0, 0); // TODO: ?
		}

		//System.out.println("Ambient color: " + ambientColor);

		// I = Kd * Ba
		Vector ambientIntensity = ambientColor.multiply(diffuseMatrix);

		// sum of all intensity sources
		Vector intensity = Vector.zeroes(3);
		intensity.add(ambientIntensity);

		// find opposite view vector and the correct surface normal
		// if negative, flip it
		Vector v = ixDirection.copy().scale(-1);
		Vector n = face.surfaceNormal();
		if (n.dot(v) < 0) { // TODO: check rounding error?
			n = n.copy().scale(-1);
		}

		light_iter:
		for (PointLight light : scene.lights()) {
			// find ray from ix point -> light source (direction vector)
			Vector l = light.position().copy()
					.sub(ix)
					.normalize();

			// check for self-occlusion
			double nL = n.dot(l);
			if (nL < 0) { // TODO: round-off error?
				System.out.println("self occluding " + nL);
				continue;
			}

			// make sure the path is clear
			for (Model m : scene.models()) {
				for (Face f : m.faces()) {
					if (f == face) {
						// don't attempt ix with the current face
						continue;
					}

					// TODO: may need different ix method for t != 0?
					// TODO: check t for rounding error?
					//if (f.intersects(ix, l)) {
					Vector subIx = f.ixPointAny(n, ix, l);
					if (f.intersects(subIx)) {
						System.out.printf("ix: %s %s\n", ix, l);
						continue light_iter;
					}
				}
			}

			System.out.println("can has light source");

			double nl = n.dot(l);

			// diffuse intensity

			// KdBl * nl
			Vector diffuseIntensity = light.rgb()
					.multiply(face.material().diffuse())
					.scale(nl);
			intensity.add(diffuseIntensity);

			// determine reflected ray
			Vector r = n.copy()
					.scale(2 * nl)
					.sub(l);

			// specular intensity
			// don't allow negative intensities - ???
			Vector specularIntensity = light.rgb()
					.scale(face.material().specularity())
					.scale(Math.pow(v.dot(r), face.material().shininess()));
			intensity.add(specularIntensity);
		}

		return intensity;
	}
	
	@Override
	public TraceResult call() throws Exception {
		Camera camera = scene.camera();
		
		// find ray from focal point -> camera point for this row,col
		// (direction vector)
		Vector l = camera.lensPoint(row, col);
		Vector e = camera.focalPoint();
		Vector unit = l.copy().sub(e).normalize();
		
		for (Model m : scene.models()) {
			for (Face f : m.faces()) {
				//System.out.println("F#" + m.faces().indexOf(f));
				Vector ix = f.ixPoint(l, unit);

				if (f.intersects(ix)) {
					Vector color = calculateLighting(f, ix, unit);

					//System.out.println("Returning: " + color);

					latch.countDown();
					return new TraceResult(row, col,
							color.val(0),
							color.val(1),
							color.val(2));
				}
			}
		}

		latch.countDown();
		return new TraceResult(row, col, 0, 0, 0);
	}
	
}
