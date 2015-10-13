package org.timothyb89.trace.math.tracer;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.timothyb89.trace.math.Camera;
import org.timothyb89.trace.math.Face;
import org.timothyb89.trace.math.Model;
import org.timothyb89.trace.math.Scene;
import org.timothyb89.trace.math.Vector;

/**
 * A trace task for a particular row and column.
 * @author timothyb
 */
public class TraceTask implements Callable<TraceResult> {

	private CountDownLatch latch;
	private Scene scene;
	private int row;
	private int col;

	public TraceTask(CountDownLatch latch, Scene scene, int row, int col) {
		this.latch = latch;
		this.scene = scene;
		this.row = row;
		this.col = col;
	}

	private Vector ixPoint(Face face, Vector l, Vector unit) {
		// N*P = -d, P = any vertex on face
		double d = -(face.surfaceNormal().dot(face.firstVertex3()));
		
		double nL = face.surfaceNormal().dot(l);
		double nU = face.surfaceNormal().dot(unit);
		// TODO: use this ^ vs nL in denom? error in slides?
		
		if (nU == 0) {
			return null;
		}
		
		double t = (d - nL) / nU; // TODO: is this right? slides error?
		
		// P = L + tU
		return l.copy().add(unit.copy().scale(t));
	}
	
	private boolean intersects(Face face, Vector l, Vector unit) {
		// assuming polys from PLY model are in order?
		Vector p = ixPoint(face, l, unit);
		if (p == null) {
			return false;
		}

		Vector[] edges = face.edgeStream()
				.map(v -> v.trim(3))
				.toArray(Vector[]::new);

		// N = e1 x e2
		Vector n = edges[0].copy().cross(edges[1]);
		
		for (int i = 0; i < face.size(); i++) {
			Vector v = face.vertex(i).trim(3);
			Vector e = edges[i];

			Vector epvj = p.copy().sub(v);
			Vector np = epvj.cross(e);

			double res = np.dot(n);
			if (res < 0) {
				// TODO: is this valid?
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public TraceResult call() throws Exception {
		Camera camera = scene.camera();
		
		// find ray from focal point -> camera point for this row,col
		Vector l = camera.lensPoint(row, col);
		Vector e = camera.focalPoint();
		Vector unit = l.copy().sub(e).normalize();
		
		for (Model m : scene.models()) {
			for (Face f : m.faces()) {
				if (intersects(f, l, unit)) {
					latch.countDown();
					return new TraceResult(row, col, 255, 255, 255);
				}
			}
		}

		latch.countDown();
		return new TraceResult(row, col, 0, 0, 0);
	}
	
}
