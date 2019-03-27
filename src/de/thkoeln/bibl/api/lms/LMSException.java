package de.thkoeln.bibl.api.lms;

/**
 * Exception for LMS interaction.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class LMSException extends Exception {

	private static final long serialVersionUID = -7412131751476123260L;
	
	/**
	 * Initialize an exception with a message.
	 * 
	 * @param msg the message describing the exception 
	 */
	public LMSException(String msg) {
		super(msg);
	}
	
	/**
	 * Initialize an exception with a message and a throwable source.
	 * 
	 * @param msg the message describing the exception
	 * @param source the exception source
	 */
	public LMSException(String msg, Throwable source) {
		super(msg, source);
	}
}
