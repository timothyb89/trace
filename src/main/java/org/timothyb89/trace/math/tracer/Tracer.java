package org.timothyb89.trace.math.tracer;

import org.timothyb89.trace.math.Camera;
import org.timothyb89.trace.math.Image;
import org.timothyb89.trace.math.Model;
import org.timothyb89.trace.math.Scene;
import org.timothyb89.trace.model.camera.CameraParser;
import org.timothyb89.trace.model.image.PPMWriter;
import org.timothyb89.trace.model.ply.PLYParser;
import org.timothyb89.trace.model.scene.SceneParser;
import org.timothyb89.trace.util.F;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author timothyb
 */
public class Tracer {

	private Scene scene;
	private Image output;

	private ExecutorService executor;

	public Tracer(Scene scene) {
		this.scene = scene;
		
		int threads = Runtime.getRuntime().availableProcessors();
		System.out.printf("Using %d thread%s\n",
				threads,
				threads == 1 ? "" : "s");
		
		//executor = Executors.newSingleThreadExecutor();
		//executor = Executors.newFixedThreadPool(4);
		executor = Executors.newFixedThreadPool(threads);
	}

	private int scale(double val, double min, double max) {
		double range = max - min;
		double off = val - min;

		return (int) (255.0 * (off / range));
	}

	public void trace() {
		Camera camera = scene.camera();

		CountDownLatch latch = new CountDownLatch(camera.width() * camera.height());

		List<Future<TraceResult>> tasks = new ArrayList<>();
		int[] bounds = camera.bounds();
		for (int u = bounds[0]; u <= bounds[2]; u++) {
			for (int v = bounds[1]; v <= bounds[3]; v++) {
				tasks.add(executor.submit(new TraceTask(latch, scene, v, u)));
			}
		}

		int total = tasks.size();
		long startTime = System.currentTimeMillis();
		System.out.printf("Submitted %d trace tasks, starting...\n", tasks.size());

		try {
			latch.await(1, TimeUnit.SECONDS);
		} catch (InterruptedException ignored) { }

		while (latch.getCount() > 0) {
			try {
				double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
				double rate = (total - latch.getCount()) / elapsed;

				System.out.printf(
						"[Trace] %-5d tasks remain (%4.1f%%). " +
								"%3.0fs elapsed, " +
								"%3.1fs est @ %.1f tasks/sec.\n",
						latch.getCount(),
						(1 - (double) latch.getCount() / (double) total) * 100,
						elapsed,
						latch.getCount() / rate,
						rate);

				latch.await(3, TimeUnit.SECONDS);
			} catch (InterruptedException ex) {
				System.err.printf(
						"[Warn] Trace interrupted with %d tasks remaining\n",
						latch.getCount());
				break;
			}
		}

		executor.shutdown();

		System.out.println("Trace complete, generating image...");

		// collect results + determine min/max intensity
		double minIntensity = 0;
		double maxIntensity = 0;
		boolean first = true;

		List<TraceResult> results = new LinkedList<>();
		for (Future<TraceResult> result : tasks) {
			try {
				TraceResult r = result.get();
				double max = Math.max(r.red(), Math.max(r.green(), r.blue()));
				double min = Math.min(r.red(), Math.min(r.green(), r.blue()));

				if (first || max > maxIntensity) {
					maxIntensity = max;
				}

				if (first || min < minIntensity) {
					minIntensity = min;
				}

				first = false;
				results.add(result.get());
			} catch (InterruptedException | ExecutionException e) {
				// ignore?
				e.printStackTrace();
			}
		}

		// generate image
		output = new Image(camera.width(), camera.height());

		for (TraceResult r : results) {
			int red = scale(r.red(), minIntensity, maxIntensity);
			int green = scale(r.green(), minIntensity, maxIntensity);
			int blue = scale(r.blue(), minIntensity, maxIntensity);

			output.set(
					r.col() - bounds[0], r.row() - bounds[1],
					red, green, blue);
		}
	}

	public Image output() {
		return output;
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			System.err.println("Usage: tracer [camera] [scene] [models...] [output]");
			System.exit(1);
			return;
		}

		Camera camera = CameraParser.readPath(Paths.get(args[0])).camera();
		System.out.println("Loaded camera: " + camera);

		// load models (1+ required)
		List<Model> models = new ArrayList<>();
		for (int i = 2; i < args.length - 1; i++) {
			Model model = PLYParser.readPath(Paths.get(args[i])).toModel();
			models.add(model);

			System.out.printf("Loaded model: %s (%d faces)\n", args[i], model.countFaces());
		}

		// create scene + apply config
		Scene scene = new Scene(camera, models);
		SceneParser.readPath(scene, Paths.get(args[1]));
		System.out.printf(
				"Loaded scene configuration: %d model%s, %d light source%s\n",
				scene.models().size(),
				scene.models().size() == 1? "" : "s",
				scene.lights().size(),
				scene.lights().size() == 1? "" : "s");

		Tracer tracer = new Tracer(scene);
		F.timeVoid(tracer::trace).thenAcceptTime(time -> {
			System.out.printf("Trace completed in %.3f seconds\n", time);
		});

		System.out.print("Writing output ...");
		F.timeVoid(() -> {
			PPMWriter.write(tracer.output(), Paths.get(args[args.length - 1]));
		}).thenAcceptTime(time -> {
			System.out.printf("done, written in %.3fs to %s.\n",
					time,
					args[args.length - 1]);
		});
	}

}
