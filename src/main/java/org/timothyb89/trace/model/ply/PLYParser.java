package org.timothyb89.trace.model.ply;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import lombok.ToString;
import org.timothyb89.trace.math.Face;
import org.timothyb89.trace.math.Matrix;
import org.timothyb89.trace.math.Model;
import org.timothyb89.trace.math.Transform;
import org.timothyb89.trace.math.Vector;
import static org.timothyb89.trace.model.ParseUtil.*;
import static org.timothyb89.trace.model.ply.PLYParseUtil.*;

/**
 *
 * @author timothyb
 */
@ToString(of = {"state", "elements"})
public class PLYParser {
	
	private List<String> rawLines;
	private List<PLYElement> elements;
	
	private State state;
	private PLYElement currentElement;
	
	private int bodyElementIndex;
	private int bodyEntryIndex;
	
	public PLYParser() {
		rawLines = new ArrayList<>();
		elements = new ArrayList<>();
		state = headerMagic;
	}
	
	public void read(String line) throws PLYParseException {
		line = line.trim();
		if (line.isEmpty()) {
			return;
		}
		
		rawLines.add(line);
		
		try {
			State newState = state.handle(line);
			if (newState != null) {
				state = newState;
			}
		} catch (PLYParseException ex) {
			throw new PLYParseException(
					String.format(
							"Error, line #%d: %s",
							rawLines.size(),
							ex.getMessage()),
					ex);
		}
	}
	
	private final State bodyElement = (line) -> {
		PLYElement e = elements.get(bodyElementIndex);
		if (bodyEntryIndex >= e.length()) {
			bodyElementIndex++;

			if (bodyElementIndex >= elements.size()) {
				throw new PLYParseException(
						"Data exceeds specified header elements!");
			}

			e = elements.get(bodyElementIndex);

			bodyEntryIndex = 0;
		}

		e.addEntry(line);
		bodyEntryIndex++;
		
		return null;
	};
	
	private final State headerElementInner = (line) -> expect(line,
			COMMENT, // allow, but no-op
			directive("property", args -> {
				currentElement.addProperty(args);
				return null; // no state change
			}),
			directive("element", args -> {
				currentElement = new PLYElement(args);
				elements.add(currentElement);
				return null;
			}),
			exact("end_header", () -> {
				bodyElementIndex = 0;
				bodyEntryIndex = 0;

				return bodyElement;
			}));
	
	private final State headerElementOuter = (line) -> expect(line,
			COMMENT, // allow, but no-op
			directive("element", args -> {
				currentElement = new PLYElement(args);
				elements.add(currentElement);

				return headerElementInner;
			}));
	
	private final State headerFormat = (line) -> expect(line,
			exact("format ascii 1.0", () -> headerElementOuter));
	
	private final State headerMagic = (line) -> expect(line,
			exact("ply", () -> headerFormat));

	public PLYElement element(String name) {
		for (PLYElement e : elements) {
			if (e.name().equalsIgnoreCase(name)) {
				return e;
			}
		}
		
		return null;
	}
	
	public Model toModel() {
		PLYElement vertex = element("vertex");
		if (vertex == null) {
			throw new PLYParseException("No vertex element found in PLY file!");
		}
		
		PLYElement face = element("face");
		if (face == null) {
			throw new PLYParseException("No face element found in PLY file!");
		}
		
		Model model = new Model();
		
		Matrix vertices = new Matrix(4, vertex.length());
		vertices.row(0, vertex.doubleValues("x").toArray());
		vertices.row(1, vertex.doubleValues("y").toArray());
		vertices.row(2, vertex.doubleValues("z").toArray());
		vertices.row(3, DoubleStream
				.generate(() -> 1.0)
				.limit(vertex.length())
				.toArray());
		model.vertexData(vertices);
		
		model.faces(face.listValues("face", Integer.class)
				.map(l -> (List<Integer>) l)
				.map(l -> new Face(model, l))
				.collect(Collectors.toList()));

		return model;
	}

	public static PLYParser readPath(Path path) throws PLYParseException {
		PLYParser parser = new PLYParser();
		
		try (Stream<String> lines = Files.lines(path)) {
			lines.forEach(parser::read);
			
			return parser;
		} catch (IOException ex) {
			throw new PLYParseException(
					"Unable to read file: " + ex.getMessage(),
					ex);
		}
	}
	
	public static void main(String[] args) {
		PLYParser p = readPath(Paths.get("data/beethoven.ply"));
		Model m = p.toModel();
		
		System.out.println("Vertices:" + m.countVertices());
		System.out.println("Center of Mass: " + m.centerMass().format());
		
		System.out.println("---");
		
		m.transform(Transform.axisRotate(Vector.of(1, 1, 1), Math.PI / 4));
		m.transform(Transform.scale(2, 0.5, 0.5));
		m.transform(Transform.translate(10, 10, 10));
		
		System.out.println("CoM: " + m.centerMass().format());
		
		PLYWriter.write(m, Paths.get("data/beethoven_r.ply"));
	}
	
}
