package de.thkoeln.bibl.api.lms.slnp.transfer;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.message.ResponseMessage;

/**
 * Implements a generic SLNP response. A Response can be used to
 * receive data from the SLNP server. It uses messages of type
 * ResponseMessage to communicate with the SLNP server.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * @param <T> type of messages the Response uses for communication
 */
public class Response<T extends ResponseMessage> extends Transfer<T> {
	
	private final Class<T> handler;
	private final Constructor<T> msgBuilder;
	 
	/**
	 * Initialize a new Response with an object that implements the  
	 * Connectable interface.
	 * 
	 * @param con a reference to a Connectable object. This reference will
	 * be used to communicate with the server
	 * @param handler the response message class which builds the messages
	 * @throws ReflectiveOperationException if the supplied response message class
	 * dosn't support the required initialization function
	 */
	public Response(Connectable con, Class<T> handler) 
			throws ReflectiveOperationException {
		
		super(con);
		
		this.handler = handler;
		this.msgBuilder = handler.getDeclaredConstructor(String.class);
	}
	
	/**
	 * Receives the response from the SLNP server using the supplied
	 * connection.
	 * 
	 * @param con a reference to a Connectable object. This reference will
	 * be used to communicate with the server
	 * @throws IOException if an I/O error occurs
	 * @throws ReflectiveOperationException if the received data has an invalid
	 * format for the defined response message type
	 */
	public void receive(Connectable con) throws IOException, 
			ReflectiveOperationException {
		
		BufferedReader in = getReader();
		String line;
		
		// read data from server
		while ((line = in.readLine()) != null) {
			
			// create new request message
			T m = msgBuilder.newInstance(line);
			
			// add message
			addMessage(m);
			
			// break if message is end marker
			if (m.isEndMarker()) break;
		}
	}
	
	/**
	 * Receives the response from the SLNP server.
	 * 
	 * @throws IOException if an I/O error occurs
	 * @throws ReflectiveOperationException if the received data has an invalid
	 * format for the defined response message type
	 */
	public void receive() throws IOException, ReflectiveOperationException {
		receive(getConnection());
	}
	
	/**
	 * Returns the response message class which builds the messages.
	 * 
	 * @return the response message class
	 */
	public Class<T> getHandler() {
		return handler;
	}
	
	/**
	 * Returns the SLNP error message if the response failed or null if
	 * request was successful.
	 * 
	 * @return the SLNP error message or null
	 */
	public String getError() {
		if (isFailed()) return firstMessage().getValue();
		return null;
	}
	
	/**
	 * Checks if the response failed.
	 * 
	 * @return true when failed
	 */
	public boolean isFailed() {
		// check if response includes at least one message
		if (getSize() < 1) return true;
		// check if the first message is an error
		if (firstMessage().isError()) return true;
		
		return false;
	}
	
	/**
	 * Checks if the response was successfully and has no errors.
	 * 
	 * @return true if successful
	 */
	public boolean isSuccessful() {
		return !isFailed();
	}
}
