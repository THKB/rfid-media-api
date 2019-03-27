package de.thkoeln.bibl.api.rfid.reader;

/**
 * Exception related to RFID-Reader interaction.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class ReaderException extends Exception {

	private static final long serialVersionUID = 1284475383367693118L;

	/**
	 * Initialize an exception with a message.
	 * 
	 * @param msg the message describing the exception 
	 */
	public ReaderException(String msg) {
		super(msg);
	}
	
	/**
	 * Initialize an exception with a message and a throwable source.
	 * 
	 * @param msg the message describing the exception
	 * @param source the exception source
	 */
	public ReaderException(String msg, Throwable source) {
		super(msg, source);
	}
}
