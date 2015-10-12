package org.timothyb89.trace.math.tracer;

import java.util.concurrent.Callable;
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

	private Scene scene;
	private int row;
	private int col;
	
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
		
		for (int v1 = 0; v1 < face.size(); v1++) {
			int v2 = (v1 + 1) % face.size();
			
			Vector edge = face.vertex(v2).copy().sub(face.vertex(v1));
			
			// TODO continue here
		}
		
		return false; // TODO
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
					return new TraceResult(255, 255, 255);
				}
			}
		}
		
		return new TraceResult(0, 0, 0);
	}
	
}
