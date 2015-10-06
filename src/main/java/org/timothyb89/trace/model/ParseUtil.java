package org.timothyb89.trace.model;

import org.timothyb89.trace.model.camera.CameraParseException;
import org.timothyb89.trace.model.ply.PLYParseException;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author timothyb
 */
public class ParseUtil {

	public interface State {

		State handle(String line) throws PLYParseException;

	}

	public interface DescribedFunction<T, R> extends Function<T, R> {

		default String describe() {
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

	@SafeVarargs
	public static State expect(String line, DescribedFunction<String, State>... choices)
			throws PLYParseException {
		for (Function<String, State> p : choices) {
			State newState = p.apply(line);
			if (newState != NO_MATCH) {
				return newState;
			}
		}

		String valid = Arrays.stream(choices)
				.map(DescribedFunction::describe)
				.collect(Collectors.joining(", "));

		throw new CameraParseException(String.format(
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

}
