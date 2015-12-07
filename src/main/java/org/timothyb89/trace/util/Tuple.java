package org.timothyb89.trace.util;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author tim
 */
@Data
@Accessors(fluent = true)
public class Tuple<A, B> {

	private final A a;
	private final B b;

}
