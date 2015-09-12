package org.timothyb89.trace.util;

import java.util.Collection;

/**
 * Various functional utilities.
 *
 * @author timothyb
 */
public class F {

	@FunctionalInterface
	public interface LoopWithIndexAndSizeConsumer<T> {

		void accept(T t, int i, int n);
		
	}

	public static <T> void forEach(Collection<T> collection,
			LoopWithIndexAndSizeConsumer<T> consumer) {
		int index = 0;
		for (T object : collection) {
			consumer.accept(object, index++, collection.size());
		}
	}

}
