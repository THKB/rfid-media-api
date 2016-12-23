package de.thkoeln.bibl.api.lms.slnp;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.transfer.DataResponse;

/**
 * Implements a SLNP login response. The SLNP server responds with this to
 * a LoginRequest send by the client.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class LoginResponse extends DataResponse {

	/**
	 * Initialize a new LoginResponse with an object that implements the  
	 * connectable interface.
	 * 
	 * @param con a reference to an connectable object. This reference will
	 * be used to communicate with the server
	 * @throws ReflectiveOperationException if the response message dosn't 
	 * support the required initialization functionality
	 */
	public LoginResponse(Connectable con) throws ReflectiveOperationException {
		super(con);
	}
}
