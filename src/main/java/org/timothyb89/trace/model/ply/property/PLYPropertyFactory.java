package org.timothyb89.trace.model.ply.property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.timothyb89.trace.model.ply.PLYParseException;

/**
 *
 * @author timothyb
 */
public class PLYPropertyFactory {
	
	private static PLYPropertyFactory instance;
	
	private final Map<String, Function<String, PLYProperty>> properties;
	
	private PLYPropertyFactory() {
		properties = new HashMap<>();
		
		properties.put("float32", name -> new PLYFloat32Property(name));
		properties.put("float",   properties.get("float32"));
		properties.put("int32",   name -> new PLYInt32Property(name));
		properties.put("int",     properties.get("int32"));
		properties.put("list",    name -> new PLYListProperty(name));
	}
	
	private static PLYPropertyFactory get() {
		if (instance == null) {
			instance = new PLYPropertyFactory();
		}
		
		return instance;
	}
	
	public static PLYProperty create(
			String type, String name, List<String> args) {
		Function<String, PLYProperty> func = get().properties.get(type);
		if (func == null) {
			throw new PLYParseException("Unknown property type: " + type);
		}
		
		PLYProperty ret = func.apply(name);
		if (args != null) {
			ret.init(args);
		}
		
		return ret;
	}
	
}
