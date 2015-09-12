package org.timothyb89.trace.model.ply;

import java.util.ArrayList;
import java.util.List;
import static org.timothyb89.trace.model.ply.PLYParseUtil.*;

/**
 *
 * @author timothyb
 */
public class PLYParser {
	
	private List<String> rawLines;
	
	private List<PLYElement> elements;
	private PLYElement currentElement;
	
	private int bodyElementIndex;
	private int bodyEntryIndex;
	
	public PLYParser() {
		elements = new ArrayList<>();
	}
	
	public void read(List<String> lines) throws PLYParseException {
		State state = headerMagic;
		
		int i = 1;
		try {
			for (String line : lines) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}

				State newState = state.handle(line);
				if (newState != null) {
					state = newState;
				}

				i++;
			}
		} catch (PLYParseException ex) {
			throw new PLYParseException(
					String.format("Error, line #%d: %s", i, ex.getMessage()),
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
			
			e = elements.get(bodyEntryIndex);
			
			bodyEntryIndex = 0;
		}
		
		e.addEntry(line);
		
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
					return null;
				}),
				exact("end_header", () -> {
					bodyElementIndex = 0;
					bodyEntryIndex = 0;
					
					return bodyElement;
				}));
	};
	
	private final State headerElementOuter = (line) -> {
		return expect(line,
				COMMENT, // allow, but no-op
				directive("element", args -> {
					currentElement = new PLYElement(args);
					return headerElementInner;
				}));
	};
	
	private final State headerFormat = (line) -> {
		return expect(line, exact("format ascii 1.0", () -> headerElementOuter));
	};
	
	private final State headerMagic = (line) -> {
		return expect(line, exact("ply", () -> headerFormat));
	};
	
}
