package org.timothyb89.trace.util;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.DoubleConsumer;
import java.util.function.Supplier;

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

	public static TimedCompletableFuture<Void> timeVoid(Runnable func) {
		long start = System.nanoTime();
		func.run();
		long diff = System.nanoTime() - start;

		TimedCompletableFuture<Void> ret = new TimedCompletableFuture<>();
		ret.completeTime((double) diff / 1000000000.0);
		return ret;
	}

	public static <T> TimedCompletableFuture<T> time(Supplier<T> func) {
		long start = System.nanoTime();
		T result = func.get();
		long diff = System.nanoTime() - start;

		TimedCompletableFuture<T> ret = new TimedCompletableFuture<>();
		ret.completeTime((double) diff / 1000000000.0);
		ret.complete(result);
		return ret;
	}

	public static class TimedCompletableFuture<T> extends CompletableFuture<T> {

		private DoubleConsumer timeConsumer;
		private Double time;

		public TimedCompletableFuture<T> thenAcceptTime(DoubleConsumer timeConsumer) {
			this.timeConsumer = timeConsumer;

			if (time != null) {
				timeConsumer.accept(time);
			}

			return this;
		}

		public void completeTime(Double time) {
			this.time = time;

			if (timeConsumer != null) {
				timeConsumer.accept(time);
			}
		}

	}

}
