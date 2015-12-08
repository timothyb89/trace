package org.timothyb89.trace.util;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author timothyb
 */
@Data
@Accessors(fluent = true)
public class Triple<A, B, C> {

	private final A a;
	private final B b;
	private final C c;

}
