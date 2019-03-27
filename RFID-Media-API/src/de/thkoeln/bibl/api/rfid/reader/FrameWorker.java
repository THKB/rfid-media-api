package de.thkoeln.bibl.api.rfid.reader;

import java.util.Comparator;
import java.util.List;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import de.thkoeln.bibl.api.rfid.tag.BaseTag;

/**
 * The FrameWorker do the processing after the FrmeReader scanned the tag.
 * 
 * Every scanned tag has a timestamp defining when it was scanned and a RSSI
 * showing the distance to the reader.
 * 
 * With this information the post-process can reorder a list of tags, splitted
 * by the time frame, based on the RSSI. Low RSSI values normally occurs when
 * a tag has a bigger distance to the reader. As a result the scan order is more 
 * realistic.
 * 
 * This implementation is just a proof of concept and should be optimized but
 * shows not bad results in the environment.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> the type of tags the frame worker can handle
 */
public class FrameWorker<T extends BaseTag> implements Runnable {

	private boolean isRunning;
	private Thread worker;
	private int timeFrame;
	private int procInterval;
	private FrameWorkerListener<T> listener;
	private ConcurrentHashMap<String, Entry<Long, T>> procTable;
	
	/**
	 * Initialize a new FrameWorker.
	 * 
	 * @param listener the listener who handles the events produced 
	 * by the FrameWorker
	 * @param timeFrame the time frame in milliseconds
	 * @param procInterval the interval the worker will wait between
	 * performing post processing tasks
	 */
	public FrameWorker(FrameWorkerListener<T> listener, int timeFrame, 
			int procInterval) {
		
		this.listener = listener;
		this.timeFrame = timeFrame;
		this.procInterval = procInterval;
		
		// initialize processing hash-table
		procTable = new ConcurrentHashMap<>(16, 0.75F, 2);
		
		isRunning = false;
	}
	
	/**
	 * Initialize a new FrameWorker with a default time frame of 
	 * 1000 milliseconds and a interval of 500 milliseconds.
	 * 
	 * @param listener the listener who handles the events produced 
	 * by the FrameWorker
	 */
	public FrameWorker(FrameWorkerListener<T> listener) {
		
		// initialize with default time frame and process interval
		this(listener, 1000, 500);
	}
	
	/**
	 * Starts the FrameWorker.
	 */
	public void start() {
		
		if (isRunning) return;
		isRunning = true;
		
		// create new thread
		worker = new Thread(this, "Worker Thread " + 
				getClass().getCanonicalName());
		
		worker.start();
	}
	
	/**
	 * Stops the FrameWorker.
	 */
	public void stop() {
		
		if (!isRunning) return;
		isRunning = false;
		
		synchronized (worker) {
			try { worker.notify(); worker.join(2000); }
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Sets the time frame the process used to reorder 
	 * the scanned tags.
	 * 
	 * @param timeFrame the time frame in milliseconds
	 */
	public void setTimeFrame(int timeFrame) {
		this.timeFrame = timeFrame;
	}
	
	/**
	 * Sets the interval the worker will wait between
	 * performing post processing tasks.
	 * 
	 * @param procInterval the interval in milliseconds
	 */
	public void setProcessInterval(int procInterval) {
		this.procInterval = procInterval;
	}
	
	/**
	 * Adds a new tag to the post-processing queue.
	 * 
	 * @param tag the tag to add to the queue
	 * @param tick the time tick defining when the 
	 * tag was scanned
	 */
	public void addTag(T tag, long tick) {
		procTable.put(tag.getUID(), 
				new SimpleImmutableEntry<>(tick, tag));
	}
	
	/**
	 * Adds a new tag to the post-processing queue.
	 * The current time is used as scan time.
	 * 
	 * @param tag the tag to add to the queue
	 */
	public void addTag(T tag) {
		addTag(tag, System.currentTimeMillis());
	}
	
	/**
	 * Checks if the post-processing queue contains a tag
	 * mapped to the supplied UID. 
	 * 
	 * @param uid the UID to check if a tag is mapped to
	 * @return true if the queue contains a tag mapped to the
	 * supplied UID
	 */
	public boolean containsTag(String uid) {
		return procTable.containsKey(uid);
	}
	
	/**
	 * Checks if the post-processing queue contains the
	 * supplied tag.
	 * 
	 * @param tag the tag to check
	 * @return true if the queue contains the tag 
	 */
	public boolean containsTag(T tag) {
		return containsTag(tag.getUID());
	}

	/**
	 * Returns the from the queue mapped to the supplied
	 * UID.
	 * 
	 * @param uid the UID the tag is mapped to
	 * @return the tag which is mapped to the supplied UID
	 */
	public T getTag(String uid) {
		return procTable.get(uid).getValue();
	}
	
	/**
	 * implements the post-processing logic.
	 */
	@Override
	public void run() {
		
		long minTick, maxTick, frameEnd;
		boolean newGroup = false;
		List<T> group = new Vector<>();
		
		while (isRunning) {
			
			// wait for next table check
			synchronized (worker) {
				if (!isRunning) break;
				try { worker.wait(procInterval); } catch (Exception e) {}
			}
			
			// wait if no process data available
			if (procTable.size() < 1) continue;
			
			// get oldest time-tick
			minTick = getMinTick(procTable.values());
			
			// set end time for frame
			frameEnd = minTick + timeFrame;
			
			maxTick = 0;
			// process values
			for (Entry<String, Entry<Long, T>> item : procTable.entrySet()) {

				Entry<Long, T> elm = item.getValue();
				
				long tick = elm.getKey();
				
				// calculate on the fly max-tick
				if (tick > maxTick) maxTick = tick;
				
				// skip item when not in time-frame
				if (tick > frameEnd) continue;
				
				// add tag to group
				group.add(elm.getValue());
			}
			
			// close group if the oldest item is out of the time-frame
			if (group.size() > 0 && maxTick <= System.currentTimeMillis() - timeFrame)
				newGroup = true;
			
			// sort the group by RSSI ASC
			Collections.sort(group, new Comparator<T>() {
		        public int compare(T o1, T o2) {
		        	return o2.getMaxRSSI() - o1.getMaxRSSI();
		        }
		    });

			if (newGroup) {
				// remove tags from process table
				for (T tag : group) {
					procTable.remove(tag.getUID());
				}
				// report to listener
				listener.newTagsProcessed(group);
			}
			
			newGroup = false;
			group.clear();
		}
	}
	
	/**
	 * Returns the smallest timestamp from the supplied tag list.
	 * 
	 * @param items the list of timestamp/tag entries
	 * @return the smallest found timestamp
	 */
	private long getMinTick(Iterable<Entry<Long,T>> items) {
		
		long min = Long.MAX_VALUE;
		
		// find oldest time-tick
		for (Entry<Long, T> item : items) {
			long tick = item.getKey();
			if (tick < min) min = tick;
		}
		return min;
	}
}
