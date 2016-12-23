package de.thkoeln.bibl.api.lms.slnp;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.message.MessageFormatException;
import de.thkoeln.bibl.api.lms.slnp.transfer.Request;

/**
 * Implements a SLNP login request. The login request is is send to authenticate
 * the client at the slnp server.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class LoginRequest extends Request {
	
	/**
	 * Initializes a new LoginRequest with the supplied credentials.
	 * The supplied connectable reference is used to communicate with
	 * the SLNP server.
	 * 
	 * @param con the connectable object reference
	 * @param cred the crendetials to use for authentication
	 * @throws MessageFormatException if the internally message creation
	 * fails
	 */
	public LoginRequest(Connectable con, Credentials cred) throws MessageFormatException {
		
		super(con, "SLNPSysParam", cred);
		
		addParameter("ParTyp", "BiblAllgemein");
		addParameter("SIASPasswort", cred.getPass());
	}
}
