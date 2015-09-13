package org.timothyb89.trace.model.ply.property;

/**
 *
 * @author timothyb
 */
public class PLYInt32Property extends PLYProperty<Integer> {

	public PLYInt32Property(String name) {
		super(name);
	}

	@Override
	public Integer read(String raw) {
		return Integer.parseInt(raw);
	}

	@Override
	public String write(Integer value) {
		return value.toString();
	}
	
}
