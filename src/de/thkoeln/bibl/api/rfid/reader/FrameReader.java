package de.thkoeln.bibl.api.rfid.reader;

import java.util.List;

import de.feig.FedmBrmTableItem;
import de.feig.FedmException;
import de.thkoeln.bibl.api.rfid.tag.BaseTag;
import de.thkoeln.bibl.api.rfid.tag.TagException;

/**
 * Class extends the BRMReader to maximize the scan precision. It's based 
 * on the BRMReader but adds a process to determine a more realistic 
 * scan order of the scanned tags.
 * 
 * The FrameReader scans the tags in the buffered reader mode, checks the 
 * tag against a valid RSSI and adds its to a queue for post-processing 
 * by the FrameWorker.
 * 
 * The post-processing is based on the RSSI, the size of the scan field 
 * of the reader and the speed the reader is moved over tags.
 * With this information a virtual position correction can be done.
 * 
 * For details @see FrameWorker.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> the type of tags the reader can handle
 */
public class FrameReader<T extends BaseTag> extends BRMReader<T>
	implements FrameWorkerListener<T> {

	private int fieldSize;
	private FrameWorker<T> worker;
	
	/**
	 * Initialize new Frame Reader.
	 * 
	 * @param tagClass the tag class the reader handles 
	 * @param con the connection used to communicate with the reader
	 * @param cacheSize the initial cache size for storing tags
	 * @param rssiLimit the limit RSSI a tag is processed
	 * @param scanSpeed the reader moving speed in seconds per meter
	 * @param fieldSize the reader detection field-size in centimeters
	 * @throws FedmException if the BRM table could't initialized
	 */
	public FrameReader(Class<T> tagClass, ReaderConnection con, int cacheSize, 
			int rssiLimit, int scanSpeed, int fieldSize) throws FedmException {
		
		// default read interval
		super(tagClass, con, cacheSize, rssiLimit);
		
		this.fieldSize = fieldSize;
		
		// create processing table worker
		worker = new FrameWorker<>(this);
		
		// set scan speed
		setScanSpeed(scanSpeed);
	}
	
	/**
	 * Initialize a new FrameReader with default cache size.
	 * 
	 * @param tagClass the tag class the reader handles 
	 * @param con the connection used to communicate with the reader
	 * @param rssiLimit the limit RSSI a tag is processed
	 * @param scanSpeed the reader moving speed in seconds per meter
	 * @param fieldSize the reader detection field-size in centimeters
	 * @throws FedmException if the BRM table could't initialized
	 */
	public FrameReader(Class<T> tagClass, ReaderConnection con, int rssiLimit, 
			int scanSpeed, int fieldSize) throws FedmException {
		
		// default cache size
		this(tagClass, con, 256, rssiLimit, scanSpeed, fieldSize);
	}
	
	/**
	 * Initialize a new FrameReader with default cache size, 
	 * RSSI limit of 50 dBm, a scan speed of 10 s/m and a scan
	 * filed size of 20 centimeters. 
	 * 
	 * @param tagClass the tag class the reader handles 
	 * @param con the connection used to communicate with the reader
	 * @throws FedmException if the BRM table could't initialized
	 */
	public FrameReader(Class<T> tagClass, ReaderConnection con) 
			throws FedmException {
		
		// default RSSI limit, scan speed and field size
		this(tagClass, con, 50, 10, 20);
	}
	
	
	@Override
	public void start() {
		super.start();
		worker.start();
	}
	
	@Override
	public void stop() {
		worker.stop();
		super.stop();
	}
	
	@Override
	public void setReadInterval(int readInterval) {
		super.setReadInterval(readInterval);
		worker.setProcessInterval(readInterval);
	}
	
	/**
	 * Sets the scan speed in m/s the reader is moved.
	 * 
	 * @param scanSpeed the speed in m/s
	 */
	public void setScanSpeed(int scanSpeed) {
		
		// scanSpeed * 1000 / (100 / fieldSize) / 4
		int interval = 5 * fieldSize * scanSpeed / 2;
		
		setReadInterval(interval);
		setTimeFrame(interval / 5);
	}
	
	/**
	 * Sets the time frame the worker uses to reorder the
	 * scanned tags.
	 * 
	 * @param timeFrame the time frame in milliseconds
	 */
	public void setTimeFrame(int timeFrame) {
		worker.setTimeFrame(timeFrame);
	}
	
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
				
				// skip actually processing tags
				if (worker.containsTag(uid)) continue;
				
				// inform listener about new detected tag
				newTagDetected(uid);
				
				T tag = null;
				// create new tag
				try { tag = createTag(tabItem); }
				// report error
				catch (TagException e) {
					tagError(e);
				}

				// add tag with current time-tick for processing
				worker.addTag(tag, System.currentTimeMillis());
			}
			
			// wait for next table check
			synchronized (thread) {
				if (!isRunning()) break;
				try { thread.wait(getReadInterval()); } catch (Exception e) {}
			}
		}
	}
	
	@Override
	public void newTagsProcessed(List<T> tags) {

		for (T tag : tags) {
			// verify that the tag is new
			if (containsTag(tag)) continue;
			// add new tags to cache and inform listener
			addTag(tag);
			newTagProcessed(tag);
		}
	}
}
