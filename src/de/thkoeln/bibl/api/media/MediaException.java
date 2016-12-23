package de.thkoeln.bibl.api.media;

/**
 * Exception for media interaction.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class MediaException extends Exception {

	private static final long serialVersionUID = 1901378446637491259L;
	
	/**
	 * Initialize an exception with a message.
	 * 
	 * @param msg the message describing the exception 
	 */
	public MediaException(String msg) {
		super(msg);
	}
	
	/**
	 * Initialize an exception with a message and a throwable source.
	 * 
	 * @param msg the message describing the exception
	 * @param source the exception source
	 */
	public MediaException(String msg, Throwable source) {
		super(msg, source);
	}
}
