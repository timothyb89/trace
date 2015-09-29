package org.timothyb89.trace.util;

import org.timothyb89.trace.math.Model;
import org.timothyb89.trace.model.ply.PLYWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

/**
 * @author timothyb
 */
public class WebSync {

	private static final WebSync instance = new WebSync();

	private Socket socket;
	private PrintWriter out;

	private ExecutorService pool;

	private WebSync() {
		pool = Executors.newSingleThreadExecutor();

		try {
			socket = new Socket("localhost", 4001);
			out = new PrintWriter(socket.getOutputStream());

			System.out.println("[Sync] Connected");
			out.println("hello");
			out.flush();
		} catch (IOException ex) {
			System.out.println("[Info] WebSync could not connect, disabling!");
		}
	}

	private boolean ready() {
		return instance.socket != null
				&& instance.socket.isConnected()
				&& !instance.socket.isClosed();
	}

	public WebSync _model(String name, Model model) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		PLYWriter.write(model, pw);

		String encoded = Base64.getEncoder().encodeToString(sw.toString().getBytes());

		out.print("model ");
		out.print(name);
		out.print(" ");
		out.print(encoded);
		out.println();
		out.flush();

		return this;
	}

	public static void model(String name, Model model) {
		if (instance.ready()) {
			synchronized(instance) {
				instance._model(name, model);
			}
		}
	}

	public WebSync _remove(String name) {
		out.print("remove ");
		out.print(name);
		out.println();
		out.flush();

		return this;
	}

	public static void remove(String name) {
		if (instance.ready()) {
			synchronized (instance) {
				instance._remove(name);
			}
		}
	}

	public WebSync _clear() {
		out.println("clear");
		out.flush();

		return this;
	}

	public static void clear() {
		if (instance.ready()) {
			synchronized (instance) {
				instance._clear();
			}
		}
	}

	public static void m(String name, Supplier<Model> s) {
		if (instance.ready()) {
			instance.pool.submit(() -> {
				Model result = s.get();

				synchronized (instance) {
					model(name, result);
				}
			});
		}
	}

}
