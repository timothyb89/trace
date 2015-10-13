package org.timothyb89.trace.math.tracer;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author timothyb
 */
@Data
@Accessors(fluent = true)
public class TraceResult {

	private final int row;
	private final int col;
	private final int red;
	private final int green;
	private final int blue;
	
}
