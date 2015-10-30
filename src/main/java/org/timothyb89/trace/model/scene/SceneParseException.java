package org.timothyb89.trace.model.scene;

/**
 * @author timothyb
 */
public class SceneParseException extends RuntimeException {

	/**
	 * Creates a new instance of <code>SceneParseException</code> without detail
	 * message.
	 */
	public SceneParseException() {
	}

	/**
	 * Constructs an instance of <code>SceneParseException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public SceneParseException(String msg) {
		super(msg);
	}

	public SceneParseException(String message, Throwable cause) {
		super(message, cause);
	}


}
