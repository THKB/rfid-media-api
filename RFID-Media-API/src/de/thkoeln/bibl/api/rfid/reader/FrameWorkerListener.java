package de.thkoeln.bibl.api.rfid.reader;

import java.util.EventListener;
import java.util.List;

import de.thkoeln.bibl.api.rfid.tag.BaseTag;

/**
 * Listener interface implementation for FrameWorker events.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> the type of tags the listener can handle
 */
public interface FrameWorkerListener<T extends BaseTag> extends EventListener {

	/**
	 * Invoked when new tags were successfully processed by the
	 * frame worker.
	 * 
	 * @param tags the list of tags that were processed by the
	 * frame worker
	 */
	public void newTagsProcessed(List<T> tags);
}
