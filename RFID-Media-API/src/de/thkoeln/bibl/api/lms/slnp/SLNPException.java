package de.thkoeln.bibl.api.lms.slnp;

import de.thkoeln.bibl.api.lms.LMSException;

/**
 * Exception for SLNP interaction.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class SLNPException extends LMSException {

	private static final long serialVersionUID = -6132354753474400790L;
	
	/**
	 * Initialize an exception with a message.
	 * 
	 * @param msg the message describing the exception 
	 */
	public SLNPException(String msg) {
		super(msg);
	}
	
	/**
	 * Initialize an exception with a message and a throwable source.
	 * 
	 * @param msg the message describing the exception
	 * @param source the exception source
	 */
	public SLNPException(String msg, Throwable source) {
		super(msg, source);
	}
}
