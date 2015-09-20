package org.timothyb89.trace.model.ply.property;

import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 * @author timothyb
 */
@ToString
@Accessors(fluent = true)
public abstract class PLYProperty<T> {
	
	@Getter
	private final String name;
	
	@Getter
	private final int index;

	public PLYProperty(String name, int index) {
		this.name = name;
		this.index = index;
	}
	
	public PLYProperty<T> init(List<String> args) {
		return this;
	}
	
	public abstract T read(String raw);
	public abstract String write(T value);
	
}
