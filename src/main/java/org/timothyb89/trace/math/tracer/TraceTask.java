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
	
	@Override
	public TraceResult call() throws Exception {
		Camera camera = scene.camera();
		
		// find ray from focal point -> camera point for this row,col
		Vector l = camera.lensPoint(row, col);
		Vector e = camera.focalPoint();
		Vector unit = l.copy().sub(e).normalize();
		
		for (Model m : scene.models()) {
			for (Face f : m.faces()) {
				if (f.intersects(l, unit)) {
					latch.countDown();
					return new TraceResult(row, col, 255, 255, 255);
				}
			}
		}

		latch.countDown();
		return new TraceResult(row, col, 0, 0, 0);
	}
	
}
