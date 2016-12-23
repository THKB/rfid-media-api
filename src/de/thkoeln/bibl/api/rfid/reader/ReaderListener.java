package de.thkoeln.bibl.api.rfid.reader;

import java.util.EventListener;

import de.thkoeln.bibl.api.rfid.tag.BaseTag;
import de.thkoeln.bibl.api.rfid.tag.TagException;

/**
 * Listener interface implementation for Reader events.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * @param <T> the type of tags the listener can handle
 * 
 */
public interface ReaderListener<T extends BaseTag> extends EventListener {
	
	/**
	 * Invoked when a tag was detected by the reader.
	 * 
	 * @param uid the UID of the detected tag
	 */
	public void tagDetected(String uid);
	
	/**
	 * Invoked when a new, previously unknown, tag was detected 
	 * by the reader.
	 * 
	 * @param uid the UID of the detected tag
	 */
	public void newTagDetected(String uid);
	
	/**
	 * Invoked when a new tag was successfully read and all 
	 * tag related data is available.
	 * 
	 * @param tag the tag that was processed by the reader
	 */
	public void newTagProcessed(T tag);
	
	/**
	 * Invoked when an error occurred while interacting 
	 * with a tag.
	 * 
	 * @param e the tag exception that caused the error
	 */
	public void tagError(TagException e);
	
	/**
	 * Invoked when an error occurred while interacting 
	 * with the reader.
	 * 
	 * @param e the reader exception that caused the error
	 */
	public void readerError(ReaderException e);
}
