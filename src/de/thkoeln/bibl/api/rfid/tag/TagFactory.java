package de.thkoeln.bibl.api.rfid.tag;

import java.lang.reflect.InvocationTargetException;

import de.feig.FedmIscReader;
import de.feig.FedmTableItem;

/**
 * Class implements the transparent creation of tag objects.
 * 
 *  @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *  
 */
public class TagFactory {
	
	/**
	 * Creates a tag from the specified tag class. The Method calls the constructor of the
	 * specified tag class with the reader and item parameter.
	 * 
	 * @param tagClass the tag class to instantiate a new tag object from
	 * @param reader the reader object will be passed to the constructor of the tag class
	 * @param item the item object will be passed to the constructor of the tag class
	 * @return a newly created tag object
	 * @throws ReflectiveOperationException if the supplied tag class doesn't implement the
	 * required constructor to initiate the tag object
	 * @throws TagException if the constructor of the tag class throws an exception
	 * @param <T> the tag type to instantiate a new tag object from
	 */
	public static <T extends BaseTag> T createTag(Class<T> tagClass, FedmIscReader reader, 
			FedmTableItem item) throws ReflectiveOperationException, TagException {
		
		T obj = null;
		
		// create tag object with constructor for specific item class ISO/BRM
		try { obj = tagClass.getDeclaredConstructor(FedmIscReader.class, item.getClass())
				.newInstance(reader, item); }
		
		// wrap exceptions thrown by the tag constructor in a TagException
		catch (InvocationTargetException e) {
			throw new TagException("could not create tag object", e.getTargetException());
		}
		
		return obj;
	}
}
