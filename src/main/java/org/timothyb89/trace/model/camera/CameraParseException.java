package org.timothyb89.trace.model.camera;

/**
 * @author timothyb
 */
public class CameraParseException extends RuntimeException {

	/**
	 * Creates a new instance of <code>PLYParseException</code> without detail
	 * message.
	 */
	public CameraParseException() {
	}

	/**
	 * Constructs an instance of <code>PLYParseException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public CameraParseException(String msg) {
		super(msg);
	}

	public CameraParseException(String message, Throwable cause) {
		super(message, cause);
	}


}
