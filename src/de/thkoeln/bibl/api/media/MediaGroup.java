package de.thkoeln.bibl.api.media;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> the type of media the group holds
 */
public class MediaGroup<T extends LibraryMedia<? extends AbstractMediaNumber>> {

	private String name;
	private Map<String, T> mediaMap;
	private TreeSet<T> mediaTree;
	
	/**
	 * Initialize a MediaGroup with a specialized comparator.
	 * The specified comparator is used to order the media
	 * in the group in a custom way.
	 * 
	 * @param name alias for the group
	 * @param comp the comparator implements the ordering for the group
	 */
	public MediaGroup(String name, Comparator<T> comp) {
		
		this.name = name;
		
		// initialize media map/tree
		mediaMap = new HashMap<>();
		mediaTree = new TreeSet<>(comp);
	}
	
	/**
	 * Initialize a MediaGroup with specialized comparator.
	 * The specified comparator is used to order the media
	 * in the group in an custom way.
	 * 
	 * @param comp the comparator implements the ordering for the group
	 */
	public MediaGroup(Comparator<T> comp) {
		this((String)null, comp);
	}
	
	/**
	 * Initialize a MediaGroup with a default sort order (natural).
	 * 
	 * @param name alias for the group
	 */
	public MediaGroup(String name) {
		this(name, (Comparator<T>)null);
	}
	
	/**
	 * Initialize a MediaGroup with a default sort order (natural).
	 */
	public MediaGroup() {
		this((String)null);
	}
	
	/**
	 * Initialize a MediaGroup with an initial list. The Media will 
	 * be sorted with default order (natural).
	 * 
	 * @param name alias for the group
	 * @param media the initial media list
	 */
	public MediaGroup(String name, Collection<T> media) {
		this(name);
		add(media);
	}
	
	/**
	 * Initialize a MediaGroup with an initial list. The Media will 
	 * be sorted with default order (natural).
	 * 
	 * @param media the initial media list
	 */
	public MediaGroup(Collection<T> media) {
		this((String)null);
		add(media);
	}
	
	/**
	 * Initialize a MediaGroup with an initial list and with a 
	 * specialized comparator. The specified comparator is used to sort
	 * the media in the group in an custom way.
	 * 
	 * @param name alias for the group
	 * @param media the initial media list
	 * @param comp the comparator implements the ordering for the group
	 */
	public MediaGroup(String name, Collection<T> media, Comparator<T> comp) {
		this(name, comp);
		add(media);
	}
	
	/**
	 * Initialize a MediaGroup with an initial list and with a 
	 * specialized comparator. The specified comparator is used to sort
	 * the media in the group in an custom way.
	 * 
	 * @param media the initial media list
	 * @param comp the comparator implements the ordering for the group
	 */
	public MediaGroup(Collection<T> media, Comparator<T> comp) {
		this(comp);
		add(media);
	}
	
	/**
	 * Adds a list of media. The media objects must be comparable. 
	 * Only objects with different media numbers will be added.
	 * 
	 * @param media list to be added
	 * @return true if all media was added
	 */
	public boolean add(Collection<T> media) {
		
		boolean skip = false;
		// add all media objects
		for (T elm : media) if (!add(elm)) skip = true;
		
		return !skip;
	}
	
	/**
	 * Adds a map of media indexed by a key. The key is not relevant for
	 * the media group and will be ignored. The media objects must be 
	 * comparable. Only objects with different media numbers will be added.
	 * 
	 * @param media map to be added
	 * @return true if all media was added
	 */
	public boolean add(Map<String, T> media) {
		return add(media.values());
	}
	
	/**
	 * Adds a media object. The media objects must be comparable.
	 * Its not possible add media objects with equal media number.
	 * 
	 * @param media to be added
	 * @return true if media was added
	 */
	public boolean add(T media) {
		
		// verify media not already added
		if (!mediaTree.add(media)) return false; 
		// add media to map
		mediaMap.put(media.getMediaNumber().getNumber(), media);
		
		return true;
	}
	
	/**
	 * Removes media from group by indexed media number.
	 * 
	 * @param mediaNr to be removed
	 */
	public void remove(String mediaNr) {
		
		// remove media by media number
		T obj = mediaMap.remove(mediaNr);
		if (obj == null) return;
		
		// remove media from tree set
		mediaTree.remove(obj);
	}
	
	/**
	 * Removes media from group by indexed media number.
	 * 
	 * @param mediaNr to be removed
	 */
	public void remove(AbstractMediaNumber mediaNr) {
		remove(mediaNr.getNumber());
	}
	
	/**
	 * Removes media from group.
	 * 
	 * @param media to be removed
	 */
	public void remove(T media) {
		remove(media.getMediaNumber());
	}
	
	/**
	 * Removes a list of media from the group.
	 * 
	 * @param media list to be removed
	 */
	public void remove(Collection<T> media) {
		for (T elm : media) remove(elm);
	}

	/**
	 * Returns the media associated with the specified media number.
	 * 
	 * @param mediaNr to get get the associated object for
	 * @return the media object or null if not found
	 */
	public T get(String mediaNr) {
		return mediaMap.get(mediaNr);
	}
	
	/**
	 * Returns the media associated with the specified media number.
	 * 
	 * @param mediaNr to get get the associated object for
	 * @return the media object or null if not found
	 */
	public T get(AbstractMediaNumber mediaNr) {
		return get(mediaNr.getNumber());
	}
	
	/**
	 * Returns the first media in the group.
	 * 
	 * @return the media
	 */
	public T getFirst() {
		
		// verify tree is not empty
		if (size() <= 0) return null;
		
		return mediaTree.first();
	}
	
	/**
	 * Returns the last media in the group.
	 * 
	 * @return the media
	 */
	public T getLast() {
		
		// verify tree is not empty
		if (size() <= 0) return null;
		
		return mediaTree.last();
	}
	
	/**
	 * Return the alias of the group or null if no alias is defined.
	 * @return group alias or null
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns all media in the group.
	 * 
	 * @return media as SortedSet
	 */
	public Set<T> getValues() {
		if (size() < 1) return Collections.emptySet();
		return mediaTree;
	}
	
	/**
	 * Returns the media index in the group specified by the 
	 * media number. If the specified media number does not exist 
	 * in the group a negative value is returned.
	 * 
	 * @param mediaNr to get the object index from
	 * @return the media index or negative value
	 */
	public int indexOf(String mediaNr) {
		
		// check if the element exist
		if (!contains(mediaNr)) return -1;
		
		// calculate position in tree-map
		return mediaTree.headSet(get(mediaNr)).size();
	}
	
	/**
	 * Returns the media index in the group specified by the 
	 * media number. If the specified media number does not exist 
	 * in the group a negative value is returned.
	 * 
	 * @param mediaNr to get the object index from
	 * @return the media index or negative value
	 */
	public int indexOf(AbstractMediaNumber mediaNr) {
		return indexOf(mediaNr.getNumber());
	}
	
	/**
	 * Returns the media index in the group. If the specified
	 * media number does not exist in the group a negative 
	 * value is returned.
	 * 
	 * @param media to get the index from
	 * @return the media index or negative value
	 */
	public int indexOf(T media) {
		return indexOf(media.getMediaNumber());
	}
	
	/**
	 * Returns the index distance between two media objects 
	 * specified by the media number. If one of the media
	 * numbers does not exist a negative value is returned.
	 * 
	 * @param mediaNr1 first media number
	 * @param mediaNr2 second media number
	 * @return the distance as positive number or negative value
	 */
	public int getDistance(String mediaNr1, String mediaNr2) {
		
		int idx1 = indexOf(mediaNr1);
		int idx2 = indexOf(mediaNr2);
		
		// verify that both objects exists
		if (idx1 < 0 || idx2 < 0) return -1;
		
		// return positive index offset
		return Math.abs(idx1 - idx2);
	}
	
	/**
	 * Returns the index distance between two media objects
	 * specified by the MediaNumber. If one of the media numbers 
	 * does not exist a negative value is returned.
	 * 
	 * @param nr1 first media number
	 * @param nr2 second media number
	 * @return the distance as positive number or negative value 
	 */
	public int getDistance(AbstractMediaNumber nr1, AbstractMediaNumber nr2) {
		return getDistance(nr1.getNumber(), nr2.getNumber());
	}
	
	/**
	 * Returns the index distance between two media objects.
	 * If one of the media objects does not exist a negative 
	 * value is returned.
	 * 
	 * @param m1 first object
	 * @param m2 second object
	 * @return the distance as positive number or negative value
	 */
	public int getDistance(T m1, T m2) {
		return getDistance(m1.getMediaNumber(), 
				m2.getMediaNumber());
	}
	
	/**
	 * Checks if the group includes a media with specified media number.
	 * 
	 * @param mediaNr the media number to check for
	 * @return true if group contains the media number
	 */
	public boolean contains(String mediaNr) {
		return mediaMap.containsKey(mediaNr);
	}
	
	/**
	 * Checks if the group includes a media with specified media number.
	 * 
	 * @param mediaNr the media number to check for
	 * @return true if group contains the media number
	 */
	public boolean contains(AbstractMediaNumber mediaNr) {
		return contains(mediaNr.getNumber());
	}
	
	/**
	 * Clears the group. After clearing the group has 0 media.
	 */
	public void clear() {
		mediaMap.clear();
		mediaTree.clear();
	}
	
	/**
	 * Returns the number of media objects in the group.
	 * 
	 * @return number of media objects
	 */
	public int size() {
		return mediaTree.size();
	}
	
	/**
	 * Return true if the group is empty,
	 * 
	 * @return true if group is empty
	 */
	public boolean isEmpty() {
		return mediaMap.isEmpty();
	}
	
	@Override
	public String toString() {
		return (name != null) ? name : super.toString();
	}
}
