package de.thkoeln.bibl.api.rfid.tag.data;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Class represents the data of a tag and implements methods to
 * manipulate this data. Sub-classes can use this functionality and
 * extend is by their specific implementation.
 * 
 * Instances of this class and sub-classes implicitly support XML
 * serializing by JAXB.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * TODO add writing support for tag data and ddm/bibliotheca data model
 */
@XmlRootElement(name="tag-data")
@XmlAccessorType(XmlAccessType.NONE)
public class TagData implements Serializable {

	private static final long serialVersionUID = -256126272869007236L;
	
	@XmlElement (name = "data", required = true)
	@XmlJavaTypeAdapter (HexBinaryAdapter.class)
	protected byte[] data;
	
	@XmlAttribute (name = "model", required = true)
	private String model;
	
	/**
	 * Default constructor for serialization.
	 */
	protected TagData() {}
	
	/**
	 * Initialize new TagData with the specified data array.
	 * The model is used to identify the type of tag-data.
	 * 
	 * @param data the data to initialize the tag-data with
	 * @param size the number of bytes to copy from the passed
	 * data array
	 * @param model the model identifying the tag-data type
	 */
	protected TagData(byte[] data, int size, String model) {
		this.model = model;
		setData(data, size);
	}
	
	/**
	 * Initialize new TagData with the specified data array.
	 * The model is used to identify the type of tag-data.
	 * 
	 * @param data the data to initialize the tag-data with
	 * @param model the model identifying the tag-data type
	 */
	public TagData(byte[] data, String model) {
		this(data, data.length, model);
	}
	
	/**
	 * Initialize new TagData with the specified data array.
	 * 
	 * @param data the data to initialize the tag-data with
	 */
	public TagData(byte[] data) {
		this(data, data.length, "RAW");
	}
	
	/**
	 * Sets the data of the tag-data. The data is copied
	 * to the internal buffer, holding the tag-data.
	 * 
	 * @param data the data to copy to the tag-data
	 * @param size the number of bytes to copy from the passed
	 * data array
	 */
	public void setData(byte[] data, int size) {
		this.data = Arrays.copyOf(data, size);
	}
	
	/**
	 * Sets the data of the tag-data. The data is copied
	 * to the internal buffer, holding the tag-data.
	 * 
	 * @param data the data to copy to the tag-data
	 */
	public void setData(byte[] data) {
		setData(data, data.length);
	}
	
	/**
	 * Sets the data of the tag-data by an passed hex encoded String.
	 * The data is copied to the internal buffer, holding the tag-data.
	 * 
	 * @param hex the hex encoded String representing the tag-data
	 */
	public void setData(String hex) {
		setData(DatatypeConverter.parseHexBinary(hex));
	}
	
	/**
	 * Returns the tag-data as a copy.
	 * 
	 * @return a copy of the tag-data
	 */
	public byte[] getData() {
		return data.clone();
	}
	
	/**
	 * Returns the model of this tag-data.
	 * 
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	
	/**
	 * Returns the tag-data as unsigned byte at a specific
	 * index.
	 * 
	 * @param idx the index to get the data from
	 * @return the data byte
	 */
	protected int getData(int idx) {
		return data[idx] & 0xFF;
	}
	
	/**
	 * Returns a specific part of the tag-data as copy.
	 * 
	 * @param idx the starting index to get the data from
	 * @param len number of bytes to get
	 * @return a copy of the data range
	 */
	protected String getDataRange(int idx, int len) {
		return new String(Arrays.copyOfRange(data, idx, idx + len)).trim();
	}
	
	/**
	 * Returns unique hash over the complete tag-data.
	 * 
	 * @return hash of tag data
	 */
	@Override
	public int hashCode() {
		return data.hashCode();
	}
	
	/**
	 * Returns the tag-data as hex encoded String.
	 * 
	 * @return the data as hex String
	 */
	@Override
	public String toString() {
		
		StringBuilder builder = new StringBuilder();
		
		for (byte b : data)
			builder.append(String.format("%02x", b));
		
		return builder.toString();
	}
	
	/**
	 * Serialize the object to an output stream as XML representation.
	 * 
	 * @param out the output stream used for serializing the object
	 * @throws JAXBException if the object could't serialized
	 */
	public void serializeTo(OutputStream out) throws JAXBException {
		
		Marshaller marshaller = JAXBContext.newInstance(this.getClass())
				.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(this, out);
	}
}
