package de.thkoeln.bibl.api.lms.slnp.transfer;

import java.io.PrintWriter;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.Credentials;
import de.thkoeln.bibl.api.lms.slnp.message.MessageFormatException;
import de.thkoeln.bibl.api.lms.slnp.message.RequestMessage;

/**
 * Implements a generic SLNP request. A Request can be used to
 * request data from the SLNP server. It uses messages of type
 * RequestMessage to communicate with the SLNP server.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class Request extends Transfer<RequestMessage> {
	
	private static final String REQ_MSG_END = "SLNPEndCommand\r\n";	
	
	/**
	 * Initialize a new Request with an object that implements the  
	 * Connectable interface. The Request don't use a protocol level
	 * authentication at the SLNP server. The supplied command describes
	 * the SLNP command used for this request.
	 * 
	 * @param con a reference to a Connectable object. This reference will
	 * be used to communicate with the server
	 * @param cmd the command used for this request
	 * @throws MessageFormatException if the supplied command has an invalid format
	 */
	public Request(Connectable con, String cmd) throws MessageFormatException {
		
		super(con);
		
		// add SLNP command as first message
		addMessage(new RequestMessage(cmd));
	}
	
	/**
	 * Initialize a new Request with an object that implements the  
	 * Connectable interface. The Request uses the supplied credentials to 
	 * do a protocol level authentication at the SLNP server. The supplied
	 * command describes the SLNP command used for this request.
	 * 
	 * @param con a reference to a Connectable object. This reference will
	 * be used to communicate with the server
	 * @param cmd the command used for this request
	 * @param cred credentials used for a protocol level authentication
	 * @throws MessageFormatException if the supplied command has an invalid format
	 */
	public Request(Connectable con, String cmd, Credentials cred) 
			throws MessageFormatException {
		
		this(con, cmd);
		
		// add protocol level authentication
		addParameter("LoginKennung", cred.getUser());
		addParameter("ZweigStelle", cred.getBranchID());
	}
	
	/**
	 * Adds a new parameter to the request. The method transparently
	 * creates a RequestMessage from the supplied key and value.
	 * 
	 * @param key the key used for creating the message
	 * @param value the value used for creating the message
	 * @throws MessageFormatException if the created message has a invalid format
	 */
	public void addParameter(String key, String value) 
			throws MessageFormatException {
		
		addMessage(new RequestMessage(key, value));
	}
	
	/**
	 * Adds a new parameter to the request. The method transparently
	 * creates a RequestMessage from the supplied key and value.
	 * 
	 * @param key the key used for creating the message
	 * @param value the value used for creating the message
	 * @throws MessageFormatException if the created message has a invalid format
	 */
	public void addParameter(String key, int value) 
			throws MessageFormatException {
		
		addParameter(key, Integer.toString(value));
	}
	
	/**
	 * Sends the request to the SLNP server using the supplied connection.
	 * 
	 * @param con a reference to a Connectable object. This reference will
	 * be used to communicate with the server
	 */
	public void send(Connectable con) {
		
		PrintWriter out = getWriter();
		
		out.print(getSLNPMessage());
		out.print(REQ_MSG_END);
		out.flush();
	}
	
	/**
	 * Sends the request to the SLNP server.
	 */
	public void send() {
		send(getConnection());
	}
}
