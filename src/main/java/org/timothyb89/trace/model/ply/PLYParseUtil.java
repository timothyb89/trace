package org.timothyb89.trace.model.ply;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * @author timothyb
 */
public class PLYParseUtil {
	
	public static interface State {
		
		State handle(String line) throws PLYParseException;
		
	}
	
	public static State expect(String line, Function<String, State>... choices)
			throws PLYParseException {
		for (Function<String, State> p : choices) {
			State newState = p.apply(line);
			if (newState != null) {
				return newState;
			}
		}
		
		throw new PLYParseException("Unexpected value for line: " + line);
	}
	
	public static Function<String, State> exact(
			String string, Supplier<State> onMatch) {
		
		return s -> {
			if (string.equalsIgnoreCase(s)) {
				if (onMatch != null) {
					return onMatch.get();
				}
			}
			
			return null;
		};
	}
	
	public static Function<String, State> directive(
			String name, Function<List<String>, State> onMatch) {
		return s -> {
			String[] tokens = s.split(" ");
			if (tokens.length > 0 && tokens[0].equalsIgnoreCase(name)) {
				return onMatch.apply(Arrays
						.asList(tokens)
						.subList(1, tokens.length - 1));
			}
			
			return null;
		};
	}
	
	public static final Function<String, State> COMMENT = directive(
			"comment",
			args -> null);
	
}
