package de.thkoeln.bibl.api.validation;

/**
 * Exception related to validation tasks.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class ValidationException extends Exception {

	private static final long serialVersionUID = 6863159979633050387L;
	
	/**
	 * Initialize an exception with a message.
	 * 
	 * @param msg the message describing the exception 
	 */
	public ValidationException(String msg) {
		super(msg);
	}
	
	/**
	 * Initialize an exception with a message and a throwable source.
	 * 
	 * @param msg the message describing the exception
	 * @param source the exception source
	 */
	public ValidationException(String msg, Throwable source) {
		super(msg, source);
	}
}
