package org.timothyb89.trace.model.ply.property;

import java.util.List;
import lombok.Getter;
import lombok.ToString;

/**
 *
 * @author timothyb
 */
@ToString
public abstract class PLYProperty<T> {
	
	@Getter
	private final String name;

	public PLYProperty(String name) {
		this.name = name;
	}
	
	public PLYProperty<T> init(List<String> args) {
		return this;
	}
	
	public abstract T read(String raw);
	public abstract String write(T value);
	
}
