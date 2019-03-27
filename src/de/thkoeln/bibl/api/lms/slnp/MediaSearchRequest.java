package de.thkoeln.bibl.api.lms.slnp;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.message.MessageFormatException;
import de.thkoeln.bibl.api.lms.slnp.transfer.Request;

/**
 * Implements a SLNP media search request. With this request a media search
 * or lookup can be initiated at the SLNP server.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class MediaSearchRequest extends Request {
	
	/**
	 * The search type determine the search in the SLNP back-end.
	 */
	public static enum SearchType {
		
		/** Search by the signature. Supports wildcards. */
		SIGNATURE("RSignatur"),
		
		/** Search by the media number. Dosn't supports wildcards. */
		MEDIA_NUMBER("RMedienNummer");
		
		private String key;
		
		private SearchType(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
	}
	
	/**
	 * Creates a media search request to lookup media information for a query 
	 * specified by searchStr. The searchType specifies the type of the search
	 * on the SLNP target. A search by signature doesn't support wildcard pattern,
	 * while a search by media number supports wildcard pattern but can't include
	 * the location code. The Result includes all media over all branch locations.
	 * 
	 * @param con connection to use for the request
	 * @param cred credentials to use for the request
	 * @param searchStr the media signature pattern to search for
	 * @param searchType specifies the type of search
	 * @param maxHit maximum hits the search should produce
	 * @param hitStart start index in the hits
	 * @throws MessageFormatException if the internally message creation fails 
	 */
	public MediaSearchRequest(Connectable con, Credentials cred, String searchStr, 
			SearchType searchType, int maxHit, int hitStart) throws MessageFormatException {
		
		super(con, "SLNPBuchDatenRecherche", cred);
		
		addParameter("RechKey", searchType.getKey());
		addParameter("RechBegriff", searchStr);
		addParameter("HitFrom", hitStart);
		addParameter("HitMaximum", maxHit);
		addParameter("HitTo", (hitStart + maxHit) - 1);
	}
	
	/**
	 * Creates a media search request to lookup media information for a single 
	 * media by the signature. The search don't support wildcard pattern.
	 * The Result includes media from all branch locations.
	 * 
	 * @param con connection to use for the request
	 * @param cred credentials to use for the request
	 * @param mediaID the media number to search for
	 * @throws MessageFormatException if the internally message creation fails 
	 */
	public MediaSearchRequest(Connectable con, Credentials cred, String mediaID) 
			throws MessageFormatException {
		
		this(con, cred, mediaID, SearchType.MEDIA_NUMBER, 1, 1);
	}
	
	/**
	 * Creates a media search request to lookup media information for a media group 
	 * specified by the signature. The search support wildcard pattern.
	 * Its not allowed to include the location code in the search signature. 
	 * The Result includes media from all branch locations.
	 * 
	 * @param con connection to use for the request
	 * @param cred credentials to use for the request
	 * @param signature the media signature pattern to search for
	 * @param maxHit maximum hits the search should produce
	 * @param hitStart start index in the hits
	 * @throws MessageFormatException if the internally message creation fails 
	 */
	public MediaSearchRequest(Connectable con, Credentials cred, String signature, 
			int maxHit, int hitStart) throws MessageFormatException {
		
		this(con, cred, signature, SearchType.SIGNATURE, maxHit, hitStart);
	}
	
	/**
	 * Creates a media search request to lookup media information for a media group 
	 * specified by the signature. The search support wildcard pattern.
	 * Its not allowed to include the location code in the search signature. 
	 * The Result includes media from all branch locations.
	 * 
	 * @param con connection to use for the request
	 * @param cred credentials to use for the request
	 * @param signature the media signature pattern to search for
	 * @param maxHit maximum hits the search should produce
	 * @throws MessageFormatException if the internally message creation fails 
	 */
	public MediaSearchRequest(Connectable con, Credentials cred, String signature, 
			int maxHit) throws MessageFormatException {
		
		this(con, cred, signature, maxHit, 1);
	}
}