package org.timothyb89.trace.model.ply;

import org.timothyb89.trace.model.ply.property.PLYListProperty;
import org.timothyb89.trace.model.ply.property.PLYProperty;
import org.timothyb89.trace.model.ply.property.PLYPropertyFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 *
 * @author timothyb
 */
@ToString(of = {"name", "length"})
@Accessors(fluent = true)
public class PLYElement {
	
	@Getter private final String name;
	@Getter private final int length;
	
	private final List<PLYProperty> properties;
	private final Map<String, PLYProperty> propertyMap;
	private final List<List<Object>> entries;
	
	public PLYElement(String name, int length) {
		this.name = name;
		this.length = length;
		
		properties = new ArrayList<>();
		propertyMap = new HashMap<>();
		entries = new ArrayList<>();
	}
	
	public PLYElement(List<String> args) {
		if (args.size() != 2) {
			throw new PLYParseException(
					"Unsupported arguments for element defintion: "
							+ String.join(" ", args));
		}
		
		name = args.get(0);
		length = Integer.parseInt(args.get(1));
		
		properties = new ArrayList<>();
		propertyMap = new HashMap<>();
		entries = new ArrayList<>();
	}
	
	public void addProperty(List<String> args) {
		if (properties.size() > 0
				&& properties.get(0) instanceof PLYListProperty) {
			throw new PLYParseException("Cannot add properties to element "
					+ "already containing a list.");
		}
		
		if (args.size() < 2) {
			throw new PLYParseException(
					"Invalid number of arguments for property:"
							+ String.join(" ", args));
		}
		
		String type = args.get(0);
		String propName = args.get(args.size() - 1);
		List<String> propArgs = args.subList(1, args.size() - 1);
		
		PLYProperty p = PLYPropertyFactory.create(type, propName, propArgs);
		if (p instanceof PLYListProperty) {
			if (!properties.isEmpty()) {
				throw new PLYParseException("List properties cannot be defined "
						+ "in elements containing other properties.");
			}
		}
		
		properties.add(p);
		propertyMap.put(p.getName(), p);
	}
	
	public void addEntry(String line) {
		if (properties.isEmpty()) {
			throw new PLYParseException("Unable to add entry to element '"
					+ name + "' with no properties defined.");
		}
		
		List<Object> entry = new ArrayList<>();
		if (properties.get(0) instanceof PLYListProperty) {
			PLYListProperty list = (PLYListProperty) properties.get(0);
			
			entry.add(list.read(line));
		} else {
			StringTokenizer st = new StringTokenizer(line, " ");
			if (st.countTokens() != properties.size()) {
				throw new PLYParseException("Tokens found in line do not match "
						+ "properties defined in header.");
			}
			
			int index = 0;
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				PLYProperty property = properties.get(index);
				
				entry.add(property.read(token));
				
				index++;
			}
		}
		
		entries.add(entry);
	}
	
}
