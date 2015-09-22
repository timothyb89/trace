package org.timothyb89.trace.util;

import org.timothyb89.trace.math.Matrix;
import org.timothyb89.trace.math.Model;
import org.timothyb89.trace.math.Transform;
import org.timothyb89.trace.math.Vector;
import org.timothyb89.trace.model.ply.PLYParser;
import org.timothyb89.trace.model.ply.PLYWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author timothyb
 */
public class ModelREPL {

	private Path input;
	private Path output;

	private Map<String, Consumer<double[]>> commands;

	private Model model;
	private Matrix transform;

	public ModelREPL(Path input, Path output) {
		this.input = input;
		this.output = output;

		commands = new HashMap<>();
		commands.put("s", this::scale);
		commands.put("t", this::translate);
		commands.put("r", this::rotate);
		commands.put("w", this::write);
		commands.put("?", this::help);

		System.out.printf("Loading model ... ");
		F.timeVoid(() -> {
			PLYParser parser = PLYParser.readPath(input);
			model = parser.toModel();
		}).thenAcceptTime(time -> {
			System.out.printf("done in %.3f seconds.\n", time);
		});

		transform = Transform.identity();
	}

	public void printInfo() {
		System.out.printf("Model info:\n");
		System.out.printf("    %-11s %-24d %-11s %s\n",
				"Vertices:", model.countVertices(),
				"Faces:", model.countFaces());

		Matrix bounds = model.boundingBox();
		System.out.printf("    %-11s %-24s %-11s %s\n",
				"Min Bounds:", bounds.vectorCol(0).format(),
				"Max Bounds:", bounds.vectorCol(1).format());
		System.out.printf("    %48s %-24s\n", "Center of Mass:",
				model.centerMass().format());
		System.out.println("Run '?' for help.\n");
	}

	public void scale(double[] args) {
		if (args.length != 3) {
			System.err.println("Error: invalid args. Usage: S [x] [y] [z]");
			return;
		}

		transform = transform.multiply(Transform.scale(
				args[0], args[1], args[2]));
	}

	public void translate(double[] args) {
		if (args.length != 3) {
			System.err.println("Error: invalid args. Usage: T [x] [y] [z]");
			return;
		}

		transform = transform.multiply(Transform.translate(
				args[0], args[1], args[2]));
	}

	public void rotate(double[] args) {
		transform = transform.multiply(Transform.axisRotate(
				Vector.of(args[0], args[1], args[2]),
				Math.toRadians(args[3])));
	}

	public void write(double[] args) {
		if (args.length != 0) {
			System.err.println("Error: command '?' accepts no arguments.");
			return;
		}

		System.out.printf("Transforming ... ");
		F.time(() -> model.transform(transform))
				.thenAcceptTime(time -> {
					System.out.printf("done in %.3fs, writing to disk ... ", time);
				})
				.thenAccept(m -> {
					PLYWriter.write(m, output);
					System.out.println("done, exiting...");
					System.exit(0);
				});
	}

	public void help(double[] args) {
		if (args.length != 0) {
			System.err.println("Error: command '?' accepts no arguments.");
			return;
		}

		System.out.println("Commands:");
		System.out.println("    S [x] [y] [z]     - scale each axis by respective factor");
		System.out.println("    T [x] [y] [z]     - translate x, y, z units");
		System.out.println("    R [x] [y] [z] [t] - rotate about axis [x,y,z] by [t]heta degrees");
		System.out.println("    W                 - perform transforms and write to output file");
	}

	public void run(String command, double[] args) {
		String lower = command.toLowerCase();

		Consumer<double[]> func = commands.getOrDefault(lower, x -> {
			System.err.printf("Error: unknown command: '%s', args: %s\n",
					lower, Arrays.toString(x));
		});

		func.accept(args);
	}

	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		if (args.length != 2) {
			System.err.println("Error: Usage: [input file] [output file]");
			System.exit(1);
			return;
		}

		Path path = Paths.get(args[0]);
		if (!Files.isReadable(path)) {
			System.err.println("Error: Unable to read model: " + args[1]);
			System.exit(1);
			return;
		}

		Path output = Paths.get(args[1]);

		ModelREPL repl = new ModelREPL(path, output);
		repl.printInfo();

		System.out.print("> ");

		String line;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split(" ");

			if (tokens.length == 0 || tokens[0].trim().isEmpty()) {
				System.out.print("> ");
				continue;
			}

			try {
				String command = tokens[0];
				double[] params = Arrays.stream(tokens)
						.skip(1)
						.mapToDouble(Double::parseDouble)
						.toArray();

				repl.run(command, params);
			} catch (NumberFormatException ex) {
				System.err.println("Error: invalid argument, must be valid float.");
			}

			System.out.print("> ");
		}
	}

}
