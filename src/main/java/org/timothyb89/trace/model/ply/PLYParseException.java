package org.timothyb89.trace.model.ply;

/**
 *
 * @author timothyb
 */
public class PLYParseException extends Exception {

	/**
	 * Creates a new instance of <code>PLYParseException</code> without detail
	 * message.
	 */
	public PLYParseException() {
	}

	/**
	 * Constructs an instance of <code>PLYParseException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public PLYParseException(String msg) {
		super(msg);
	}

	public PLYParseException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
