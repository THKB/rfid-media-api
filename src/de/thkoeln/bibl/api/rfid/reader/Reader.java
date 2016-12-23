package de.thkoeln.bibl.api.rfid.reader;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import de.feig.FedmIscReader;
import de.feig.FedmTableItem;
import de.feig.TagHandler.FedmIscTagHandler;
import de.thkoeln.bibl.api.rfid.tag.BaseTag;
import de.thkoeln.bibl.api.rfid.tag.TagException;
import de.thkoeln.bibl.api.rfid.tag.TagFactory;

/**
 * Class implements common functionality of a RFID reader.
 * Specializing sub-classes must implement the abstract parts of this
 * class. A reader can signal scan related events to registered listeners.
 * 
 * Added tags were transparently stored for later offline access. the whole 
 * tag-data is stored in an hash-map for fast access and to guaranty only 
 * unique stored tags.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * @param <T> the type of tags the reader can handle
 */
public abstract class Reader<T extends BaseTag> {
	
	protected FedmIscReader reader;
	
	private ReaderConnection con;
	private Map<String, ? super T> tags;
	private Class<T> tagClass;
	private List<ReaderListener<? super T>> listener;
	
	/**
	 * Initialize a new Reader.
	 * 
	 * @param tagClass the tag class the reader handles 
	 * @param con the connection used to communicate with the reader
	 * @param cacheSize the initial cache size for storing tags
	 */
	public Reader(Class<T> tagClass, ReaderConnection con, int cacheSize) {
		
		this.con = con;
		this.reader = con.reader;
		this.tagClass = tagClass;
		
		// initialize tag cache
		tags = new Hashtable<>(cacheSize);
		
		// initialize listener list
		listener = new Vector<>();
	}
	
	/**
	 * Add a new listener the reader will inform about events.
	 * 
	 * @param listener the listener to register
	 */
	public void addListener(ReaderListener<? super T> listener) {
		this.listener.add(listener);
	}
	
	/**
	 * Removes a registered listener from the event notification 
	 * registry.
	 * 
	 * @param listener the listener to remove
	 */
	public void removeListener(ReaderListener<? super T> listener) {
		this.listener.remove(listener);
	}
	
	/**
	 * Returns the connection the reader used for communication 
	 * with the reader.
	 * 
	 * @return the reader connection
	 */
	public ReaderConnection getConnection() {
		return con;
	}
	
	/**
	 * Returns a map of tags from the internal map.
	 * The tags are indexed by their UID.
	 * 
	 * @return a map with tags
	 */
	public Map<String, ? super T> getTags() {
		return tags;
	}
	
	/**
	 * Add a tag to the internal map. The tag will be
	 * indexed by it's UID.
	 * 
	 * @param uid the UID of the tag
	 * @param tag the tag to add
	 */
	protected void addTag(String uid, T tag) {
		tags.put(uid, tag);
	}
	
	/**
	 * Add a tag to the internal map. The tag will be
	 * indexed by it's UID.
	 * 
	 * @param tag the tag to add
	 */
	protected void addTag(T tag) {
		addTag(tag.getUID(), tag);
	}
	
	/**
	 * Add a tag to the internal map. The tag will internally
	 * created with the supplied table item.
	 * 
	 * @param uid the UID to map the tag to
	 * @param item the table item from which the tag will be created
	 * @return the created tag that was added to the internal map
	 * @throws TagException if the internal tag creation failed
	 */
	protected T addTag(String uid, FedmTableItem item) throws TagException {

		T tag = createTag(item);
		tags.put(uid, tag);

		return tag;
	}
	
	/**
	 * Add a tag to the internal map. The tag will internally
	 * created with the supplied tag handler.
	 * 
	 * @param uid the UID to map the tag to
	 * @param handler the tag handler from which the tag will be created
	 * @return the created tag that was added to the internal map
	 * @throws TagException if the internal tag creation failed
	 */
	protected T addTag(String uid, FedmIscTagHandler handler) throws TagException {
		return addTag(uid, handler.getTabItem());
	}

	/**
	 * Report a detected tag to all registered listeners.
	 * 
	 * @param uid the UID of the detected tag
	 */
	protected void tagDetected(String uid) {
		for (ReaderListener<? super T> lis : listener)
			lis.tagDetected(uid);
	}

	/**
	 * Report a detected tag that was previously unknown to the reader 
	 * to all registered listeners.
	 * 
	 * @param uid the UID of the detected tag
	 */
	protected void newTagDetected(String uid) {
		for (ReaderListener<? super T> lis : listener)
			lis.newTagDetected(uid);
	}

	/**
	 * Report a processed tag to all registered listeners.
	 * 
	 * @param tag the tag that was processed by the reader
	 */
	protected void newTagProcessed(T tag) {
		for (ReaderListener<? super T> lis : listener)
			lis.newTagProcessed(tag);
	}

	/**
	 * Report a tag related error to all registered listeners.
	 * 
	 * @param e the tag exception that caused the error
	 */
	protected void tagError(TagException e) {
		for (ReaderListener<? super T> lis : listener)
			lis.tagError(e);
	}

	/**
	 * Report a reader related error to all registered listeners.
	 * 
	 * @param e the reader exception that caused the error
	 */
	protected void readerError(ReaderException e) {
		for (ReaderListener<? super T> lis : listener)
			lis.readerError(e);
	}
	
	/**
	 * Creates a new tag object from the supplied table item.
	 * 
	 * @param item the table item used to create the tag
	 * @return the created tag
	 * @throws TagException if the creation failed
	 */
	protected T createTag(FedmTableItem item) throws TagException {
		
		T tag = null;
		
		// create new tag
		try { tag = TagFactory.createTag(
				getTagClass(), reader, item); }
		
		catch (Exception e) {
			throw new TagException("could not create tag", e);
		}
		
		return tag;
	}
	
	/**
	 * Returns the tag class the reader can handle.
	 * 
	 * @return the tag class
	 */
	public Class<T> getTagClass() {
		return tagClass;
	}
	
	/**
	 * Checks if the reader has a tag stored in the internal map
	 * which is mapped to the supplied UID.
	 * 
	 * @param uid the UID to check if a tag is mapped to
	 * @return true if a tag exists that is mapped to the supplied UID
	 */
	public boolean containsTag(String uid) {
		return tags.containsKey(uid);
	}
	
	/**
	 * Checks if the reader has the supplied tag stored in the 
	 * internal map.
	 * 
	 * @param tag the tag to check for
	 * @return true if the tag exists in the internal map
	 */
	public boolean containsTag(T tag) {
		return tags.containsValue(tag);
	}
	
	/**
	 * Clear the internal tag map.
	 */
	public void clearCache() {
		tags.clear();
	}
	
	/**
	 * Starts the reader and tag detection. After starting the
	 * reader, the event reporting automatically reports to registered
	 * listeners.
	 */
	public abstract void start();
	
	/**
	 * Stops the reader and tag detection.
	 */
	public abstract void stop();
}
