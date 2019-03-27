package de.thkoeln.bibl.api.lms.slnp.message;

/**
 * Exception for SLNP message interaction.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class MessageFormatException extends Exception {
	
	private static final long serialVersionUID = 5989364113663581817L;

	/**
	 * Initialize an exception with a message.
	 * 
	 * @param msg the message describing the exception 
	 */
	public MessageFormatException(String msg) {
		super(msg);
	}
}
