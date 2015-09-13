package org.timothyb89.trace.model.ply.property;

/**
 *
 * @author timothyb
 */
public class PLYFloat32Property extends PLYProperty<Float> {

	public PLYFloat32Property(String name) {
		super(name);
	}

	@Override
	public Float read(String raw) {
		return Float.valueOf(raw);
	}
	
	@Override
	public String write(Float value) {
		return value.toString();
	}
	
}
