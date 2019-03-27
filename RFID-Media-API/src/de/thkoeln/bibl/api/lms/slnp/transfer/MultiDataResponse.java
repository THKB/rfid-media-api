package de.thkoeln.bibl.api.lms.slnp.transfer;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.message.ResponseMessage;
import de.thkoeln.bibl.api.lms.slnp.message.ResponseMessage.MessageCode;

/**
 * Implements a SLNP multi-data response. It's extends the single key/value 
 * DataResponse to handle multiple data key/value maps.
 *
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class MultiDataResponse extends DataResponse {

	private String idxName;
	private Map<String, Map<String, String>> dataSet;
	
	/**
	 * Initialize a new MultiDataResponse with an object that implements the  
	 * connectable interface. The index name defines which SLNP message key
	 * should be used to index the data.
	 * 
	 * @param con a reference to a connectable object. This reference will
	 * be used to communicate with the server
	 * @param idxName case sensitive name to index data sets by a specific data key name
	 * @throws ReflectiveOperationException if the response message dosn't 
	 * support the required initialization functionality
	 */
	public MultiDataResponse(Connectable con, String idxName) 
			throws ReflectiveOperationException {
		
		super(con);
		
		this.idxName = idxName.trim();
		
		// initialize data storage map
		dataSet = new HashMap<>();
	}
	
	@Override
	public void parse() {
		
		// parse single data response
		super.parse();
		
		HashMap<String, String> set = new HashMap<>();
		String idxKey = null;
		
		// check all messages for multi-data messages
		for (ResponseMessage m : getMessages()) {
			
			// multi-data message found
			if (m.getCode() == MessageCode.DATA_MULTI) {
				// store response message in data set
				set.put(m.getKey(), m.getValue());
				// keep value of index key
				if (m.getKey().equals(idxName)) idxKey = m.getValue();
			}
			// end of data set
			else if (m.getCode() == MessageCode.DATA_SEPARATOR || 
					 m.getCode() == MessageCode.SUCCEED_EOD) {
				// store new data set with index key
				if (set.size() > 0) dataSet.put(idxKey, set);
				// prepare new data set
				set = new HashMap<String, String>();
			}
		}
	}
	
	/**
	 * Returns all data sets as indexed map.
	 * 
	 * @return all data sets
	 */
	public Map<String, Map<String, String>> getDataSet() {
		return dataSet;
	}
	
	/**
	 * Returns a specific data set by an index key.
	 *  
	 * @param key specifies the key for which the data set 
	 * should be fetched for
	 * @return the data set indexed by the key
	 */
	public Map<String, String> getDataSet(String key) {
		return dataSet.get(key);
	}
	
	/**
	 * Returns all data sets as iterable entry set.
	 * 
	 * @return an iterable entry set
	 */
	public Set<Entry<String, Map<String, String>>> getEntrySet() {
		return dataSet.entrySet();
	}
}
