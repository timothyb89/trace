package org.timothyb89.trace.model.ply;

import static org.timothyb89.trace.model.ParseUtil.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
/**
 *
 * @author timothyb
 */
public class PLYParseUtil {
	
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
