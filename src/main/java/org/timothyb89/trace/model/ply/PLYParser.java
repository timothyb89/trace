package org.timothyb89.trace.model.ply;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import lombok.ToString;
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
			System.out.println(Arrays.toString(elements.toArray()));
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
	
	private final State headerElementInner = (line) -> {
		return expect(line,
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
	};
	
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

	public Model toModel() {

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
		PLYParser p = readPath(Paths.get("data/octahedron.ply"));
	}
	
}
