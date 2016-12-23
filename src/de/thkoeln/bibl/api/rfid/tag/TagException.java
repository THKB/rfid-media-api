package de.thkoeln.bibl.api.rfid.tag;

/**
 * Exception for RFID-Tag related interaction.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class TagException extends Exception {
	
	private static final long serialVersionUID = -7192572877365894038L;
	
	/**
	 * Initialize an exception with a message.
	 * 
	 * @param msg the message describing the exception 
	 */
	public TagException(String msg) {
		super(msg);
	}
	
	/**
	 * Initialize an exception with a message and a throwable source.
	 * 
	 * @param msg the message describing the exception
	 * @param source the exception source
	 */
	public TagException(String msg, Throwable source) {
		super(msg, source);
	}
}
