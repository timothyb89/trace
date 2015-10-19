package org.timothyb89.trace.model.camera;

import java.util.function.Function;
import java.util.stream.Stream;

import static org.timothyb89.trace.model.ParseUtil.*;

/**
 * @author timothyb
 */
public class CameraParseUtil {

	public static DescribedFunction<String, State> ints(
			int length, Function<int[], State> onMatch) {
		return describe(
				"int[" + length + "]",
				s -> {
					int[] params = Stream.of(s.split(" "))
							.mapToInt(Integer::parseInt)
							.toArray();
					if (params.length == length) {
						return onMatch.apply(params);
					}

					return NO_MATCH;
				});
	}
	
	public static DescribedFunction<String, State> bounds(
			Function<int[], State> onMatch) {
		return describe(
				"int[4] -> bounds[-u, -v, +u, +v]",
				s -> {
					int[] params = Stream.of(s.split(" "))
							.mapToInt(Integer::parseInt)
							.toArray();
					
					if (params.length != 4) {
						return NO_MATCH;
					}
					
					if (params[0] >= params[2]) {
						return NO_MATCH;
					}
					
					if (params[1] >= params[3]) {
						return NO_MATCH;
					}
					
					return onMatch.apply(params);
				});
	}

	public static DescribedFunction<String, State> doubles(
			int length, Function<double[], State> onMatch) {
		return describe(
				"double[" + length + "]",
				s -> {
					double[] params = Stream.of(s.split(" "))
							.mapToDouble(Double::parseDouble)
							.toArray();
					if (params.length == length) {
						return onMatch.apply(params);
					}

					return NO_MATCH;
				});
	}

}
