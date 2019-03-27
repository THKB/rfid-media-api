package de.thkoeln.bibl.api.rfid.reader;

import de.feig.FedmBrmTableItem;
import de.feig.FedmException;
import de.feig.FedmIscReaderConst;
import de.feig.FedmIscReaderID;
import de.feig.FedmTableItem;
import de.thkoeln.bibl.api.rfid.tag.BaseTag;
import de.thkoeln.bibl.api.rfid.tag.TagException;

/**
 * Class implements a RFID reader that uses the buffered reader mode to read
 * tag data. The BRMReader use the functionality of the ThreadedReader
 * and scan and process in the background. Events will be reported through
 * the listener registry implemented in the Reader implementation.
 * 
 * A reader in buffered reader mode periodically asks the reader for scanned 
 * RFID tags. The Reader itself managed the scan process. A limitation in the 
 * buffered reader mode is the unavailability of the AFI. Tags scanned with the
 * BRMReader don't have a AFI.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * @param <T> the type of tags the reader can handle
 */
public class BRMReader<T extends BaseTag> extends ThreadedReader<T> {

	private int tabSize;
	private int rssiLimit;
	
	/**
	 * Initialize a new BRMReader.
	 * 
	 * @param tagClass the tag class the reader handles 
	 * @param con the connection used to communicate with the reader
	 * @param cacheSize the initial cache size for storing tags
	 * @param rssiLimit the limit RSSI a tag is processed
	 * @throws FedmException if the BRM table could't initialized
	 */
	public BRMReader(Class<T> tagClass, ReaderConnection con, int cacheSize, 
			int rssiLimit) throws FedmException {
		
		// default read interval
		super(tagClass, con, cacheSize);
		
		tabSize = cacheSize;
		this.rssiLimit = rssiLimit;
		
		// limit BRM table size
		if (tabSize > 200) tabSize = 200;
		
		// set BRM table size
		reader.setTableSize(FedmIscReaderConst.BRM_TABLE, tabSize);
	}
	
	/**
	 * Initialize a new BRMReader with default cache size.
	 * 
	 * @param tagClass the tag class the reader handles 
	 * @param con the connection used to communicate with the reader
	 * @param rssiLimit the limit RSSI a tag is processed
	 * @throws FedmException if the BRM table could't initialized
	 */
	public BRMReader(Class<T> tagClass, ReaderConnection con, int rssiLimit) 
			throws FedmException {
		
		// default cache size
		this(tagClass, con, 256, rssiLimit);
	}
	
	/**
	 * Initialize a new BRMReader with default cache size and 
	 * RSSI limit of 50 dBm.
	 * 
	 * @param tagClass the tag class the reader handles 
	 * @param con the connection used to communicate with the reader
	 * @throws FedmException if the BRM table could't initialized
	 */
	public BRMReader(Class<T> tagClass, ReaderConnection con) 
			throws FedmException {
		
		// default RSSI limit
		this(tagClass, con, 50);
	}
	
	/**
	 * Sets the RSSI limit a tag is processed and not dropped.
	 * 
	 * @param rssiLimit the limit in dBm
	 */
	public void setRSSILimit(int rssiLimit) {
		this.rssiLimit = rssiLimit;
	}
	
	/**
	 * Implements the scan task logic. Thread management is done by the
	 * ThreadedReader.
	 */
	@Override
	public void run() {
		
		FedmBrmTableItem[] tabItems = null;
		
		// get worker thread object
		Thread thread = getWorker();
		
		// reset local tag storage cache
		clearCache();
		
		// initialize BRM reader mode
		try { initReader(); }
		// report error and stop worker
		catch (ReaderException e) {
			readerError(e);
			stop();
			return;
		}
		
		// worker main loop
		while (isRunning()) {
						
			// get new table items
			try { tabItems = getTableItems(); }
			// report error and retry
			catch (ReaderException e) {
				readerError(e);
				continue;
			}
				
			// process all new items
			for (FedmBrmTableItem tabItem : tabItems) {
								
				// verify that RSSI is in valid range
				try { if (!isRSSIValid(tabItem)) continue; }
				// report error
				catch (TagException e) {
					tagError(e);
				}
				
				// get UID from tag
				String uid = tabItem.getUid();
				
				// inform listener about detected tag
				tagDetected(uid);
				
				// skip already cached tags
				if (containsTag(uid)) continue;
				
				// inform listener about new detected tag
				newTagDetected(uid);
				
				try {
					// create new tag object
					T tag = addTag(uid, tabItem);
					// inform listener about new tag object
					newTagProcessed(tag);
				}
				catch (TagException e) {
					tagError(e);
				}
			}
			
			// wait for next table check
			synchronized (thread) {
				if (!isRunning()) break;
				try { thread.wait(getReadInterval()); } catch (Exception e) {}
			}
		}
	}
	
	/**
	 * Initialize the assigned reader in BRM mode.
	 * 
	 * @throws ReaderException if the reader could't initialized
	 */
	protected void initReader() throws ReaderException {
		
		// set BRM table size
		reader.setData(FedmIscReaderID.FEDM_ISC_TMP_ADV_BRM_SETS, tabSize);
		
		// initialize BRM table
		try { reader.sendProtocol((byte)0x33); }
		
		catch (Exception e) {
			throw new ReaderException("could not initialize buffer", e);
		}
	}
	
	/**
	 * Returns the BRM table items as array or an empty array if the 
	 * table has no items. The table will be automatically updated 
	 * and cleared after successfully reading.
	 * 
	 * @return the table items array
	 * @throws ReaderException if table items could't processed 
	 */
	protected FedmBrmTableItem[] getTableItems() throws ReaderException {
		
		int size = 0;
		
		// read BRM table
		try { reader.sendProtocol((byte)0x22); }
		
		catch (Exception e) {
			throw new ReaderException("could not read table", e);
		}
		
		// get table length
		try { size = reader.getTableLength(
				FedmIscReaderConst.BRM_TABLE); }
		
		catch (FedmException e) {
			throw new ReaderException("could not get table size", e);
		}
		
		// return empty array
		if (size < 1) return new FedmBrmTableItem[0];
		
		FedmBrmTableItem[] items = null;
		
		// get table items
		try { items = (FedmBrmTableItem[])reader.getTable(
				FedmIscReaderConst.BRM_TABLE); }
		
		catch (FedmException e) {
			throw new ReaderException("could not read table items", e);
		}
		
		// clear BRM table
		try { reader.sendProtocol((byte)0x32); }
		
		catch (Exception e) {
			throw new ReaderException("could not clear table", e);
		}
		
		return items;
	}

	/**
	 * Checks if the supplied RSSI value is in a valid range.
	 * 
	 * @param rssi the RSSI value to check
	 * @return true if the supplied RSSI is valid
	 */
	protected boolean isRSSIValid(int rssi) {
		return (rssi <= 0 || rssi >= rssiLimit);
	}
	
	/**
	 * Checks if the RSSI value of the supplied table item is 
	 * in a valid range.
	 * 
	 * @param item the item to check the RSSI from
	 * @return true if the table item has a valid RSSI
	 * @throws TagException if the reading of the RSSI failed
	 */
	protected boolean isRSSIValid(FedmTableItem item) throws TagException {
		return isRSSIValid(BaseTag.getMaxRSSI(item));
	}
}
