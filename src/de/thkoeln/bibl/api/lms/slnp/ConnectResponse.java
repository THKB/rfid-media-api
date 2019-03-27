package de.thkoeln.bibl.api.lms.slnp;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.message.ConnectResponseMessage;
import de.thkoeln.bibl.api.lms.slnp.transfer.Response;

/**
 * Implements SLNP connect response. The SLNP server sends a response
 * after client opens a connection to the server.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class ConnectResponse extends Response<ConnectResponseMessage> {
	
	/**
	 * Initialize a new ConnectResponse with an object that implements the  
	 * connectable interface.
	 * 
	 * @param con a reference to a connectable object. This reference will
	 * be used to communicate with the server
	 * @throws ReflectiveOperationException if the response message dosn't 
	 * support the required initialization functionality
	 */
	public ConnectResponse(Connectable con) throws ReflectiveOperationException {
		super(con, ConnectResponseMessage.class);
	}
	
	/**
	 * Returns the server response message.
	 * 
	 * @return the server response message
	 */
	public ConnectResponseMessage getMessage() {
		return firstMessage();
	}
}
