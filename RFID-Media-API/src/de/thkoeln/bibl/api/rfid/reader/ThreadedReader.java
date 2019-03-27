package de.thkoeln.bibl.api.rfid.reader;

import de.thkoeln.bibl.api.rfid.tag.BaseTag;

/**
 * Class adds threading support to the generic Reader. A ThreadedReader can
 * starts a background process to perform reader tasks.
 * 
 * The sub-class must implement the task logic. The thread handling is done by
 * the ThreadedReader.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> the type of tags the reader can handle
 */
public abstract class ThreadedReader<T extends BaseTag> extends Reader<T>
		implements Runnable {
	
	private boolean isRunning;
	private int readInterval;
	private Thread worker;
	
	/**
	 * Initialize a new ThreadedReader.
	 * 
	 * @param tagClass the tag class the reader handles
	 * @param con the connection used to communicate with the reader
	 * @param cacheSize the initial cache size for storing tags
	 * @param readInterval the interval the reader will wait between
	 * performing read tasks
	 */
	public ThreadedReader(Class<T> tagClass, ReaderConnection con, 
			int cacheSize, int readInterval) {
		
		super(tagClass, con, cacheSize);
		
		this.readInterval = readInterval;
		isRunning = false;
	}
	
	/**
	 * Initialize a new ThreadedReader with a default interval.
	 * 
	 * @param tagClass the tag class the reader handles
	 * @param con the connection used to communicate with the reader
	 * @param cacheSize the initial cache size for storing tags
	 */
	public ThreadedReader(Class<T> tagClass, ReaderConnection con, 
			int cacheSize) {
		
		// initialize with default process interval
		this(tagClass, con, cacheSize, 500);
	}
	
	@Override
	public void start() {
		
		if (isRunning) return;
		isRunning = true;
		
		// create new thread
		worker = new Thread(this, "Reader Thread " + 
				getClass().getCanonicalName());
		
		worker.start();
	}
	
	@Override
	public void stop() {
		
		if (!isRunning) return;
		isRunning = false;
		
		synchronized (worker) {
			try {worker.notify(); worker.join(2000);}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Checks if the reader thread is running.
	 * 
	 * @return true if the reader thread is running
	 */
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Returns the reader thread.
	 * 
	 * @return the reader thread
	 */
	public Thread getWorker() {
		return worker;
	}
	
	/**
	 * Returns the interval the reader will wait between
	 * performing read tasks.
	 * 
	 * @return the interval
	 */
	public int getReadInterval() {
		return readInterval;
	}
	
	/**
	 * Sets the interval the reader will wait between
	 * performing read tasks.
	 * 
	 * @param readInterval the interval to set
	 */
	public void setReadInterval(int readInterval) {
		this.readInterval = readInterval;
	}
}
