package de.thkoeln.bibl.api.rfid.reader;

import java.util.Map;

import de.feig.FedmException;
import de.feig.FedmIscReaderConst;
import de.feig.TagHandler.FedmIscTagHandler;
import de.thkoeln.bibl.api.rfid.tag.BaseTag;
import de.thkoeln.bibl.api.rfid.tag.TagException;
import de.thkoeln.bibl.api.rfid.tag.TagIOException;

/**
 * Class implements a RFID reader that uses the host mode to read
 * tag data. The HostReader use the functionality of the ThreadedReader
 * and scan and process in the background. Events will be reported through
 * the listener registry implemented in the Reader implementation.
 * 
 * A reader in host reader mode periodically scans for RFID tags. It uses
 * a polling technique. A limitation in the host reader mode is the 
 * unavailability of the RSSI. Tags scanned with the HostReader don't have a RSSI.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * @param <T> the type of tags the reader can handle
 */
public class HostReader<T extends BaseTag> extends ThreadedReader<T> {

	/**
	 * Initialize a new HostReader.
	 * 
	 * @param tagClass the tag class the reader handles 
	 * @param con the connection used to communicate with the reader
	 * @param cacheSize the initial cache size for storing tags
	 * @throws FedmException if the ISO table could't initialized
	 */
	public HostReader(Class<T> tagClass, ReaderConnection con, 
			int cacheSize) throws FedmException {
		
		super(tagClass, con, cacheSize);
		
		// set ISO table size
		reader.setTableSize(FedmIscReaderConst.ISO_TABLE, cacheSize);
	}
	
	/**
	 * Initialize a new HostReader with default values.
	 * 
	 * @param tagClass the tag class the reader handles 
	 * @param con the connection used to communicate with the reader
	 * @throws FedmException if the ISO table could't initialized
	 */
	public HostReader(Class<T> tagClass, ReaderConnection con) 
			throws FedmException {
		
		// default table size
		this(tagClass, con, 256);
	}
	
	/**
	 * Implements the scan task logic. Thread management is done by the
	 * ThreadedReader.
	 */
	@Override
	public void run() {
		
		Map<String, FedmIscTagHandler> map = null;
		
		// get worker thread object
		Thread thread = getWorker();
		
		while (isRunning()) {
			
			// run inventory
			try { map = reader.tagInventory(true, (byte)0, (byte)1); }
			catch (Exception e) {
				readerError(new ReaderException("could not inventory", e));
				continue;
			}
			
			// check for new tags
			for (Map.Entry<String, FedmIscTagHandler> elm : map.entrySet()) {
				
				String uid = elm.getKey();
				
				// inform listener about detected tag
				tagDetected(uid);
				
				// verify if new tag 
				if (containsTag(uid)) continue;
				
				// inform listener about new tag
				newTagDetected(uid);
				
				try {
					// create new tag object
					T tag = addTag(uid, elm.getValue());
					// inform listener about new tag object
					newTagProcessed(tag);
				}
				catch (Exception e) {
					// ignore TagIOException caused by bad tag detection
					if (e.getCause().getClass() != TagIOException.class) 
						tagError(new TagException("could not create tag", e));
				}
			}
			
			// wait for next inventory
			synchronized (thread) {
				if (!isRunning()) break;
				try {thread.wait(getReadInterval());} catch (Exception e) {}
			}
		}	
	}
}
