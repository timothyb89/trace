package org.timothyb89.trace.model.ply;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 *
 * @author timothyb
 */
@Accessors(fluent = true)
public class PLYElement {
	
	@Getter private final String name;
	@Getter private final int length;
	
	private final List<PLYProperty> properties;
	
	public PLYElement(String name, int length) {
		this.name = name;
		this.length = length;
		
		properties = new ArrayList<>();
	}
	
	public PLYElement(List<String> args) {
		throw new UnsupportedOperationException("TODO");
	}
	
	public void addProperty(List<String> args) {
		throw new UnsupportedOperationException("TODO");
	}
	
	public void addEntry(String line) {
		throw new UnsupportedOperationException("TODO");
	}
	
}
