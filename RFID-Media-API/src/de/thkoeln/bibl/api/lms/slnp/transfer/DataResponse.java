package de.thkoeln.bibl.api.lms.slnp.transfer;

import java.util.HashMap;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.message.ResponseMessage;
import de.thkoeln.bibl.api.lms.slnp.message.ResponseMessage.MessageCode;
import de.thkoeln.bibl.api.lms.slnp.message.ResponseMessage.MessageType;

/**
 * Implements a SLNP data response. It's extends the DefaultResponse
 * with it's key/value message handling.
 *
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class DataResponse extends DefaultResponse {
	
	private HashMap<String, String> data;
	
	/**
	 * Initialize a new DataResponse with an object that implements the  
	 * connectable interface.
	 * 
	 * @param con a reference to a connectable object. This reference will
	 * be used to communicate with the server
	 * @throws ReflectiveOperationException if the response message dosn't 
	 * support the required initialization functionality
	 */
	public DataResponse(Connectable con) throws ReflectiveOperationException {
		
		super(con);
		
		// initialize data storage map
		data = new HashMap<>();
	}
	
	/**
	 * Parse all received data messages.
	 */
	public void parse() {
		// loop through all messages
		for (ResponseMessage m : getMessages()) {
			if (m.getCode() == MessageCode.DATA_SINGLE)
				data.put(m.getKey(), m.getValue());
		}
	}
	
	/**
	 * Returns the parsed data as key/value map.
	 * 
	 * @return the data as map, indexed with the message key
	 */
	public HashMap<String, String> getData() {
		return data;
	}
	
	/**
	 * Returns the parsed data for a specific key.
	 * 
	 * @param key the key for which the data should be received
	 * @return the received data
	 */
	public String getData(String key) {
		return data.get(key);
	}
	
	/**
	 * Checks if the received response is a valid SLNP data response.
	 * 
	 * @see de.thkoeln.bibl.api.lms.slnp.transfer.Transfer#isValid()
	 * @return true if the response is valid
	 */
	@Override
	public boolean isValid() {
		
		// check that the response succeed
		if (super.isFailed()) return false;
		
		// check that transfered messages are valid
		if (!super.isValid()) return false;
		
		// check that all messages met the the data class
		for (ResponseMessage m : getMessages()) {
			if (m.getCode().type != MessageType.DATA)
				if (m != lastMessage())
					if (m.getCode() != MessageCode.SUCCEED_EOD) return false;
		}
		return true;
	}
}
