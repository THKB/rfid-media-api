package de.thkoeln.bibl.api.lms.slnp;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.transfer.MultiDataResponse;

/**
 * Implements a SLNP media search response. The SLNP server responds with
 * this to a MediaSearchRequest send by the client.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class MediaSearchResponse extends MultiDataResponse {
	
	/**
	 * Initialize a new MediaSearchResponse with an object that implements the  
	 * connectable interface.
	 * 
	 * @param con a reference to a connectable object. This reference will
	 * be used to communicate with the server
	 * @throws ReflectiveOperationException if the response message dosn't 
	 * support the required initialization functionality
	 */
	public MediaSearchResponse(Connectable con) 
			throws ReflectiveOperationException {
		
		super(con, "MedienNummer");
	}
	
	/**
	 * Checks if the search response was successfully.
	 * 
	 * @return true if the response was successfully.
	 */
	public boolean isSuccessful() {
		return isValid();
	}
}
