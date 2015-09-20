package org.timothyb89.trace.model.ply.property;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import lombok.ToString;
import org.timothyb89.trace.model.ply.PLYParseException;

/**
 *
 * @author timothyb
 * @param <T>
 */
@ToString
public class PLYListProperty<T> extends PLYProperty<List<T>> {

	private String indexType; // will always use int here
	private PLYProperty<T> valueType;
	
	public PLYListProperty(String name, int index) {
		super(name, index);
	}

	@Override
	public PLYProperty init(List<String> args) {
		if (args.size() != 2) {
			throw new PLYParseException("Invalid arguments for list property: "
					+ String.join(" ", args));
		}
		
		indexType = args.get(0);
		valueType = PLYPropertyFactory.create(args.get(1), "", 0, null);
		
		return this;
	}

	@Override
	public List<T> read(String raw) {
		StringTokenizer st = new StringTokenizer(raw, " ");
		try {
			int count = Integer.parseInt(st.nextToken());
			
			List<T> ret = new ArrayList<>();
			for (int i = 0; i < count; i++) {
				ret.add(valueType.read(st.nextToken()));
			}
			
			return ret;
		} catch (NoSuchElementException ex) {
			throw new PLYParseException(
					"List element has invalid number of parameters: " + raw,
					ex);
		}
	}

	@Override
	public String write(List<T> value) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(value.size());
		sb.append(" ");
		
		sb.append(value.stream()
				.map(val -> valueType.write(val))
				.collect(Collectors.joining(" ")));
		
		return sb.toString();
	}

	
	
}
