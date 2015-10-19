package org.timothyb89.trace.math.tracer;

import org.timothyb89.trace.math.Camera;
import org.timothyb89.trace.math.Image;
import org.timothyb89.trace.math.Model;
import org.timothyb89.trace.math.Scene;
import org.timothyb89.trace.model.camera.CameraParser;
import org.timothyb89.trace.model.image.PPMWriter;
import org.timothyb89.trace.model.ply.PLYParser;
import org.timothyb89.trace.util.F;

import java.nio.file.Paths;
import java.util.ArrayList;
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

		long startTime = System.currentTimeMillis();
		System.out.printf("Submitted %d trace tasks, starting...\n", tasks.size());
		while (latch.getCount() > 0) {
			try {
				double elapsed = (System.currentTimeMillis() - startTime) / 1000.0;
				System.out.printf(
						"[Trace] %d tasks remain. Elapsed time: %.3f seconds.\n",
						latch.getCount(),
						elapsed);

				latch.await(1, TimeUnit.SECONDS);
			} catch (InterruptedException ex) {
				System.err.printf(
						"[Warn] Trace interrupted with %d tasks remaining\n",
						latch.getCount());
				break;
			}
		}

		executor.shutdown();

		System.out.println("Trace complete, generating image...");
		output = new Image(camera.width(), camera.height());
		for (Future<TraceResult> result : tasks) {
			try {
				TraceResult r = result.get();

				output.set(
						r.col() - bounds[0], r.row() - bounds[1],
						r.red(), r.green(), r.blue());
			} catch (InterruptedException | ExecutionException e) {
				// ignore?
				e.printStackTrace();
			}
		}
	}

	public Image output() {
		return output;
	}

	public static void main(String[] args) {
		if (args.length < 3) {
			System.err.println("Usage: tracer [camera] [models...] [output.ppm]");
			System.exit(1);
			return;
		}

		Camera camera = CameraParser.readPath(Paths.get(args[0])).camera();
		System.out.println("Loaded camera: " + camera);

		Scene scene = new Scene();
		scene.camera(camera);

		List<Model> models = new ArrayList<>();
		for (int i = 1; i < args.length - 1; i++) {
			Model model = PLYParser.readPath(Paths.get(args[i])).toModel();
			models.add(model);

			System.out.println("Loaded model: " + args[i]);
		}

		scene.models(models);

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
