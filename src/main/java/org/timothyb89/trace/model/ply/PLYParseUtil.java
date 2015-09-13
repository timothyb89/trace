package org.timothyb89.trace.model.ply;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author timothyb
 */
public class PLYParseUtil {
	
	public static interface State {
		
		State handle(String line) throws PLYParseException;
		
	}
	
	public static interface DescribedFunction<T, R> extends Function<T, R> {
		
		public default String describe() {
			return null;
		}
		
	}
	
	public static final State NO_MATCH = (line) -> null;
	
	public static <T, R> DescribedFunction<T, R> describe(
			String desc, Function<T, R> f) {
		return new DescribedFunction<T, R>() {

			@Override
			public R apply(T t) {
				return f.apply(t);
			}


			@Override
			public String describe() {
				return desc;
			}
			
		};
	}
	
	public static State expect(String line, DescribedFunction<String, State>... choices)
			throws PLYParseException {
		for (Function<String, State> p : choices) {
			State newState = p.apply(line);
			if (newState != NO_MATCH) {
				return newState;
			}
		}
		
		String valid = Arrays.stream(choices)
				.map(func -> func.describe())
				.collect(Collectors.joining(", "));
		
		throw new PLYParseException(String.format(
				"Expected one of (%s), got: %s",
				valid,
				line));
	}
	
	public static DescribedFunction<String, State> exact(
			String string, Supplier<State> onMatch) {
		
		return describe(
				"== " + string,
				s -> {
					if (string.equalsIgnoreCase(s)) {
						if (onMatch != null) {
							return onMatch.get();
						}
					}

					return NO_MATCH;
				});
	}
	
	public static DescribedFunction<String, State> directive(
			String name, Function<List<String>, State> onMatch) {
		return describe(
				"=> " + name,
				s -> {
					String[] tokens = s.split(" ");
					if (tokens.length > 0 && tokens[0].equalsIgnoreCase(name)) {
						return onMatch.apply(Arrays
								.asList(tokens)
								.subList(1, tokens.length));
					}

					return NO_MATCH;
				});
	}
	
	public static final DescribedFunction<String, State> COMMENT = directive(
			"comment",
			args -> null);
	
}
