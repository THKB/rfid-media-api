package de.thkoeln.bibl.api.lms;

import java.util.Map;

import de.thkoeln.bibl.api.media.AbstractMediaNumber;
import de.thkoeln.bibl.api.media.LibraryMedia;

/**
 * Interface describes default methods to search media in LMS.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> the type of objects that can be searched for
 */

public interface Searchable<T extends LibraryMedia<? extends AbstractMediaNumber>> {

	/**
	 * Returns a library media object from the LMS specified by the ID or null 
	 * if no corresponding object was found by the LMS
	 * 
	 * @param id the unique ID to perform a lookup for
	 * @return a library media object or null
	 * @throws LMSException if a exception occurred in the LMS back-end 
	 * while looking up the media
	 */
	public T lookupMedia(String id) throws LMSException;
	
	/**
	 * Returns a map of library media objects from the LMS found by the string,
	 * indexed by the media signature. The signature supports wildcard pattern.
	 * 
	 * @param signature the string to perform a search for
	 * @return a map of library media objects
	 * @throws LMSException if a exception occurred in the LMS back-end 
	 * while searching for the media
	 */
	public Map<String, T> searchMedia(String signature) throws LMSException;
}
