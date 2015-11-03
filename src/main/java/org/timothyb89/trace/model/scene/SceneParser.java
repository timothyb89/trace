package org.timothyb89.trace.model.scene;

import org.timothyb89.trace.math.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author timothyb
 */
public class SceneParser {

	private Scene scene;

	private int lineCount;

	public SceneParser(Scene scene) {
		this.scene = scene;

		lineCount = 0;
	}

	private double bounded(String input, String name) throws SceneParseException {
		try {
			double value = Double.parseDouble(input);
			if (value < 0.0 || value > 1.0) {
				throw new SceneParseException(String.format(
						"Value out of range for parameter '%s': %s",
						name, input));
			}

			return value;
		} catch (NumberFormatException ex) {
			throw new SceneParseException(String.format(
					"Invalid value for parameter '%s': %s",
					name, input));
		}
	}

	private double positive(String input, String name) {
		try {
			double value = Double.parseDouble(input);
			if (value < 0.0) {
				throw new SceneParseException(String.format(
						"Value must be positive for parameter '%s': %s",
						name, input));
			}

			return value;
		} catch (NumberFormatException ex) {
			throw new SceneParseException(String.format(
					"Invalid value for parameter '%s': %s",
					name, input));
		}
	}

	private Vector parsePoint(String[] tokens) {
		if (tokens.length != 3) {
			throw new SceneParseException("Invalid arguments for point");
		}

		if (tokens[0].equals("A")
				&& tokens[1].equals("A")
				&& tokens[2].equals("A")) {
			return null;
		}

		try {
			double x = Double.parseDouble(tokens[0]);
			double y = Double.parseDouble(tokens[1]);
			double z = Double.parseDouble(tokens[2]);

			return Vector.of(x, y, z);
		} catch (NumberFormatException ex) {
			throw new SceneParseException("Invalid point parameter: "
					+ ex.getMessage());
		}
	}

	private Vector parseColor(String[] tokens) {
		if (tokens.length != 3) {
			throw new SceneParseException("Invalid arguments for color");
		}

		try {
			double red = bounded(tokens[0], "red");
			double green = bounded(tokens[1], "green");
			double blue = bounded(tokens[2], "blue");

			return Vector.of(red, green, blue);
		} catch (NumberFormatException ex) {
			throw new SceneParseException("Invalid color parameter: "
					+ ex.getMessage());
		}
	}

	private void readLight(String[] tokens) {
		// 0: 'L'
		// 1-3, incl.: rgb
		// 4-6, incl.: pos
		if (tokens.length != 7) {
			throw new SceneParseException(
					"Invalid parameter count for point light");
		}

		Vector color = parseColor(Arrays.copyOfRange(tokens, 1, 4));
		Vector position = parsePoint(Arrays.copyOfRange(tokens, 4, 7));
		PointLight light = new PointLight(color, position);

		if (position == null) {
			if (scene.ambientLight() != null) {
				System.err.println(
						"[Warn] Multiple ambient lights defined, replacing...");
			}

			scene.ambientLight(light);
		} else {
			scene.addLight(light);
		}
	}

	private void readMaterial(String[] tokens) {
		// 0: 'M'
		// 1: model index
		// 2,3: face range, incl.
		// 4,5,6: diffuse color
		// 7: specular k_s
		// 8: shininess alpha
		if (tokens.length != 9) {
			throw new SceneParseException(
					"Invalid parameter count for material");
		}

		try {
			int modelIndex = Integer.parseInt(tokens[1]);
			int faceStart = Integer.parseInt(tokens[2]);
			int faceEnd = Integer.parseInt(tokens[3]);

			Vector diffuseColor = parseColor(Arrays.copyOfRange(tokens, 4, 7));
			double specularity = bounded(tokens[7], "specularity");
			double shininess = positive(tokens[8], "shininess");

			Material mat = Material.of(diffuseColor, specularity, shininess);

			//System.out.println("Applying material to faces " + faceStart + " ... " + faceEnd);
			//System.out.println(diffuseColor);
			Model model = scene.model(modelIndex);

			int end = Math.min(faceEnd, model.countFaces() - 1);
			for (int i = faceStart; i <= end; i++) {
				model.face(i).material(mat);
			}

			//model.faces().stream()
			//		.skip(faceStart)
			//		.limit((faceEnd - faceStart) + 1)
			//		.forEach(face -> face.material(mat));
		} catch (NumberFormatException ex) {
			throw new SceneParseException("Invalid number");
		}
	}

	public void read(String  line) throws SceneParseException {
		lineCount++;

		line = line.trim();
		if (line.isEmpty()) {
			return;
		}

		try {
			String[] tokens = line.split(" ");
			String first = tokens[0].toLowerCase();

			switch (first) {
				case "l": readLight(tokens); break;
				case "m": readMaterial(tokens); break;
				default:
					throw new SceneParseException(
							"Unknown parameter type: " + tokens[0]);
			}
		} catch (SceneParseException ex) {
			throw new SceneParseException(String.format(
					"Error, line #%d: %s",
					lineCount, ex.getMessage()));
		}
	}

	public static SceneParser readPath(Scene scene, Path path)
			throws SceneParseException {
		SceneParser parser = new SceneParser(scene);

		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(parser::read);

			return parser;
		} catch (IOException ex) {
			throw new SceneParseException(
					"Unable to read file: " + ex.getMessage(),
					ex);
		}
	}

}
