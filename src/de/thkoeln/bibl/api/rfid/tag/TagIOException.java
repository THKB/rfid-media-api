package de.thkoeln.bibl.api.rfid.tag;

/**
 * Exception related to RFID-Tag I/O interaction.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class TagIOException extends TagException {

	private static final long serialVersionUID = -4809840585174785148L;
	
	/**
	 * Initialize an exception with a message.
	 * 
	 * @param msg the message describing the exception 
	 */
	public TagIOException(String msg) {
		super(msg);
	}
	
	/**
	 * Initialize an exception with a message and a throwable source.
	 * 
	 * @param msg the message describing the exception
	 * @param source the exception source
	 */
	public TagIOException(String msg, Throwable source) {
		super(msg, source);
	}
}
