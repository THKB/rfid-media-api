package de.thkoeln.bibl.api.media;

import java.io.OutputStream;
import java.io.Serializable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class implements the structure of a library media and extends the media
 * number by other media related data.
 * This class supports the serializing to/from XML data.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * @param <T> the type of media number the library media used
 */
@XmlRootElement(name="library-media")
@XmlAccessorType(XmlAccessType.NONE)
public class LibraryMedia<T extends AbstractMediaNumber> implements Serializable, 
		Comparable<LibraryMedia<T>> {
	
	private static final long serialVersionUID = -7536849076528974385L;
	
	@XmlAnyElement
	private T mediaNumber;
	
	@XmlElement (name = "author", required = true)
	private String author;
	
	@XmlElement (name = "titel", required = true)
	private String titel;
	
	@XmlElement (name = "isbn", required = false)
	private String isbn;
	
	@XmlElement (name = "year", required = true)
	private int year;
	
	/**
	 * Default constructor for serialization.
	 */
	protected LibraryMedia() {}
	
	/**
	 * Initialize a new LibraryMedia with the supplied media number.
	 * 
	 * @param mediaNumber the media number of the media
	 */
	public LibraryMedia(T mediaNumber) {
		this.mediaNumber = mediaNumber;
	}
	
	/**
	 * Returns the media number of the media.
	 * 
	 * @return the media number
	 */
	public T getMediaNumber() {
		return mediaNumber;
	}
	
	/**
	 * Returns the ID of the media. The media is identified
	 * by its media number.
	 * 
	 * @return the media ID
	 */
	@XmlAttribute (name = "id", required = true)
	public String getMediaID() {
		return mediaNumber.getNumber();
	}
	
	/**
	 * Sets the author of the media.
	 * 
	 * @param author the author to be set
	 */
	public void setAuthor(String author) {
		this.author = getSafeValue(author);
	}
	
	/**
	 * Returns the author of the media.
	 * 
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}
	
	/**
	 * Sets the title of the media.
	 * 
	 * @param title the title to be set
	 */
	public void setTitle(String title) {
		this.titel = getSafeValue(title);
	}
	
	/**
	 * Returns the title of the media.
	 * 
	 * @return the title
	 */
	public String getTitel() {
		return titel;
	}
	
	/**
	 * Sets the ISBN number of the media.
	 * 
	 * @param isbn the ISBN number to be set
	 */
	public void setISBN(String isbn) {
		this.isbn = getSafeValue(isbn);
	}
	
	/**
	 * Returns the ISBN number of the media.
	 * 
	 * @return the ISBN number
	 */
	public String getISBN() {
		return isbn;
	}
	
	/**
	 * Sets the creation year of the media.
	 * 
	 * @param year the year to be set
	 */
	public void setYear(int year) {
		this.year = year;
	}
	
	/**
	 * Returns the creation year of the media.
	 * 
	 * @return the creation year
	 */
	public int getYear() {
		return year;
	}
	
	/**
	 * Serialize the object to an output stream as XML representation.
	 * 
	 * @param out the output stream used for serializing the object
	 * @throws JAXBException if the object could't serialized
	 */
	public void serializeTo(OutputStream out) throws JAXBException {
		
		Marshaller marshaller = JAXBContext.newInstance(this.getClass(), 
				mediaNumber.getClass()).createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(this, out);
	}

	/**
	 * Compare the media by it's media number.
	 * 
	 * @see AbstractMediaNumber#compareTo(AbstractMediaNumber)
	 * @param obj the media to compare with
	 */
	@Override
	public int compareTo(LibraryMedia<T> obj) {
		return getMediaNumber().compareTo(obj.getMediaNumber());
	}
	
	@Override
	public String toString() {
		return mediaNumber.toString();
	}
	
	/**
	 * Returns the trimmed value of the supplied value
	 * but will ignore passed null values.
	 * 
	 * @param value the value to trim
	 * @return the trimmed value
	 */
	private String getSafeValue(String value) {
		if (value != null) return value.trim();
		return value;
	}
}
