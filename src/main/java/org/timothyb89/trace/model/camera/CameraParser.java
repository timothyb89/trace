package org.timothyb89.trace.model.camera;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.timothyb89.trace.math.Camera;
import org.timothyb89.trace.math.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.timothyb89.trace.model.ParseUtil.*;
import static org.timothyb89.trace.model.camera.CameraParseUtil.*;

/**
 * @author timothyb
 */
@ToString(of = {"camera"})
@Accessors(fluent = true)
public class CameraParser {

	@Getter private Camera.CameraBuilder camera;
	@Getter private State state;

	private int lineIndex;

	public CameraParser() {
		camera = Camera.builder();
		state = readFocalPoint;
		lineIndex = 0;
	}

	public void read(String line) throws CameraParseException {
		lineIndex++;
		line = line.trim();
		if (line.isEmpty()) {
			return;
		}

		try {
			State newState = state.handle(line);
			if (newState != null) {
				state = newState;
			}
		} catch (CameraParseException ex) {
			throw new CameraParseException(
					String.format(
							"Error, line #%d: %s",
							lineIndex,
							ex.getMessage()),
					ex);
		}
	}

	private final State readBounds = line -> expect(line, ints(4, values -> {
		camera.bounds(values);

		return null;
	}));

	private final State readFocalLength = line -> expect(line, doubles(1, d -> {
		camera.focalLength(d[0]);

		return readBounds;
	}));


	private final State readViewUp = line -> expect(line, doubles(3, vec -> {
		camera.viewUp(Vector.of(vec));

		return readFocalLength;
	}));

	private final State readLookAtPoint = line -> expect(line, doubles(3, point -> {
		camera.lookAtPoint(Vector.of(point));

		return readViewUp;
	}));

	private final State readFocalPoint = line -> expect(line, doubles(3, point -> {
		camera.focalPoint(Vector.of(point));

		return readLookAtPoint;
	}));

	public static CameraParser readPath(Path path) throws CameraParseException {
		CameraParser parser = new CameraParser();

		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(parser::read);

			return parser;
		} catch (IOException ex) {
			throw new CameraParseException(
					"Unable to read file: " + ex.getMessage(),
					ex);
		}
	}

	public static void main(String[] args) {
		CameraParser parser = new CameraParser();
		parser.read("0.0 0.0 0.0");
		System.out.println(parser);
		parser.read("0.0 0.0 100");
		System.out.println(parser);
		parser.read("0.0 1.0 0.0");
		System.out.println(parser);
		parser.read("340.0");
		System.out.println(parser);
		parser.read("-128 -128 127 127");
		System.out.println(parser);
	}

}
