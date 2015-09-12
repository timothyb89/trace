package org.timothyb89.trace.model.ply;

import lombok.ToString;

/**
 *
 * @author timothyb
 */
@ToString
public abstract class PLYProperty<T> {
	
	public abstract Class getType();
	
	public abstract T getValue(String raw);
	
}
