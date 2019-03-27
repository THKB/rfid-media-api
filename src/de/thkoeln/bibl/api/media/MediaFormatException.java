package de.thkoeln.bibl.api.media;

/**
 * Exception for media interaction.
 *  
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class MediaFormatException extends MediaException {

	private static final long serialVersionUID = -6944889307370130849L;
	
	/**
	 * Initialize an exception with a message.
	 * 
	 * @param msg the message describing the exception 
	 */
	public MediaFormatException(String msg) {
		super(msg);
	}
	
	/**
	 * Initialize an exception with a message and a throwable source.
	 * 
	 * @param msg the message describing the exception
	 * @param source the exception source
	 */
	public MediaFormatException(String msg, Throwable source) {
		super(msg, source);
	}
}
