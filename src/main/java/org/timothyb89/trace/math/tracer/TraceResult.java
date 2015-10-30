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
	private final double red;
	private final double green;
	private final double blue;
	
}
