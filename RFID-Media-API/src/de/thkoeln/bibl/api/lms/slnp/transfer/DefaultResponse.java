package de.thkoeln.bibl.api.lms.slnp.transfer;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.message.ResponseMessage;

/**
 * Implements a default SLNP response with it's key/value based
 * response message type.
 *
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class DefaultResponse extends Response<ResponseMessage> {
	
	/**
	 * Initialize a new DefaultResponse with an object that implements the  
	 * connectable interface.
	 * 
	 * @param con a reference to a connectable object. This reference will
	 * be used to communicate with the server
	 * @throws ReflectiveOperationException if the response message dosn't 
	 * support the required initialization functionality
	 */
	public DefaultResponse(Connectable con) throws ReflectiveOperationException {
		super(con, ResponseMessage.class);
	}
}
