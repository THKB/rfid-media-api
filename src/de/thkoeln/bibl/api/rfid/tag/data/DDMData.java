package de.thkoeln.bibl.api.rfid.tag.data;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class extends the basic tag data with the specific
 * DDM (Daenisches Daten Modell).
 * 
 * Instances of this class and sub-classes implicitly support XML
 * serializing by JAXB.
 * 
 * The DDM data layout in hex:
 * 
 * SV CC PP SS..SS CCCC NNNN II..II
 * 
 * S = media status 					4 bits
 * V = version number 					4 bits
 * C = number of media parts			1 byte
 * P = part number (zero if n = 1)		1 byte
 * S = signature						16 bytes
 * C = CRC of tag data except CRC 		2 bytes
 * N = ISO nation code					2 bytes
 * I = ISIL library code				9 bytes
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
@XmlRootElement(name="ddm-data")
@XmlAccessorType(XmlAccessType.NONE)
public class DDMData extends TagData implements Comparable<DDMData> {

	private static final long serialVersionUID = 7424992882693529077L;
	
	protected static final int DDM_SIZE = 32;
	private static final int DDM_STATUS_IDX = 0;
	private static final int DDM_PARTS_IDX = 1;
	private static final int DDM_PARTNR_IDX = 2;
	private static final int DDM_SIGNATURE_IDX = 3;
	private static final int DDM_SIGNATURE_LEN = 16;
	private static final int DDM_CRC_IDX = 19;
	private static final int DDM_CRC_LEN = 2;
	private static final int DDM_NATION_IDX = 21;
	private static final int DDM_NATION_LEN = 2;
	private static final int DDM_BIBID_IDX = 23;
	private static final int DDM_BIBID_LEN = 9;

	private CRC16 crc;
	
	/**
	 * Initialize new DDMData with the specified data array.
	 * The model is used to identify the type of tag-data.
	 * 
	 * @param data the data to initialize the data-model with
	 * @param size the number of bytes to copy from the passed
	 * data array
	 * @param model the model identifying the tag-data type
	 */
	protected DDMData(byte[] data, int size, String model) {
		
		super(data, size, model);
		
		crc = new CRC16();
		updateChecksum();
	}
	
	/**
	 * Initialize new DDMData with the specified data array.
	 * 
	 * @param data the data to initialize the data-model with
	 * @param size the number of bytes to copy from the passed
	 * data array
	 */
	protected DDMData(byte[] data, int size) {
		this(data, size, "DDM");
	}
	
	/**
	 * Initialize new DDMData with the specified data array.
	 * 
	 * @param data the data to initialize the data-model with
	 */
	public DDMData(byte[] data) {
		this(data, DDM_SIZE);
	}
	
	/**
	 * Initialize new DDMData with the tag-data from the
	 * supplied TagData.
	 * 
	 * @param data the TagData holding the data for this 
	 * data-model 
	 */
	public DDMData(TagData data) {
		this(data.getData());
	}
	
	/**
	 * Initialize new DDMData with an empty data-model.
	 */
	public DDMData() {
		this(new byte[DDM_SIZE]);
	}
	
	/**
	 * Initialize new DDMData with an hex encoded String representing
	 * the data.
	 * 
	 * @param hex the hex encoded String representing the data
	 */
	public DDMData(String hex) {
		this(DatatypeConverter.parseHexBinary(hex));
	}
	
	/**
	 * Called after finishing unmarshalling the object.
	 */
	@PostConstruct
    private void postConstruct() {
		// rebuild CRC
		crc = new CRC16();
		updateChecksum();
	}
	
	/**
	 * Returns the version number.
	 * 
	 * @return the version number
	 */
	@XmlElement (name = "version", required = true)
	public int getVersion() {
		return (getData(DDM_STATUS_IDX) & 0xF0) >> 4;
	}
	
	/**
	 * Returns the status.
	 * 
	 * @return the status
	 */
	@XmlElement (name = "status", required = true)
	public int getStatus() {
		return (getData(DDM_STATUS_IDX) & 0x0F);
	}
	
	/**
	 * Return the number of parts.
	 * 
	 * @return the number of parts
	 */
	@XmlElement (name = "parts", required = true)
	public int getParts() {
		return getData(DDM_PARTS_IDX);
	}
	
	/**
	 * Return the part number.
	 * 
	 * @return the part number
	 */
	@XmlElement (name = "part", required = true)
	public int getPartNr() {
		return getData(DDM_PARTNR_IDX) == 0 ? 1 : getData(DDM_PARTNR_IDX);
	}

	/**
	 * Return the signature.
	 * 
	 * @return the signature
	 */
	@XmlElement (name = "signature", required = true)
	public String getSignature() {
		return getDataRange(DDM_SIGNATURE_IDX, DDM_SIGNATURE_LEN);
	}
	
	/**
	 * Return the nation.
	 * 
	 * @return the nation
	 */
	@XmlElement (name = "nation", required = true)
	public String getNation() {
		return getDataRange(DDM_NATION_IDX, DDM_NATION_LEN);
	}
	
	/**
	 * Return the ISIL (International Standard Identifier 
	 * for Libraries and Related Organizations).
	 * 
	 * @return the ISIL
	 */
	@XmlElement (name = "isil", required = true)
	public String getLibID() {
		return getDataRange(DDM_BIBID_IDX, DDM_BIBID_LEN);
	}
	
	/**
	 * Checks if the DDM-data is valid. The data is validated
	 * with the checksum, which is included in the data-model.
	 * 
	 * @return true if the data is valid
	 */
	@XmlElement (name = "valid", required = true)
	public boolean isValid() {
		return (crc.getValue() == getCRC());
	}
	
	/**
	 * Returns the CRC-16 of the data.
	 * 
	 * @return the CRC-16 of the data
	 */
	@XmlElement (name = "crc", required = true)
	public int getCRC() {
		 // LSB first
		return (getData(DDM_CRC_IDX + 1) << 8) | (getData(DDM_CRC_IDX));
	}
	
	@Override
	public void setData(byte[] data) {
		setData(data, DDM_SIZE);
		updateChecksum();
	}
	
	/**
	 * Updates the CRC-16 checksum of the DDM-data.
	 */
	protected void updateChecksum() {
		crc.reset();
		for (int i=0; i < DDM_SIZE; i++) {
			// skip CRC bytes
			if (i >= DDM_CRC_IDX && i <= (DDM_CRC_IDX + DDM_CRC_LEN - 1)) continue;
			crc.update(data[i]);
		}
		// append null bytes to fill up DDM Size
		for (int i=0; i < DDM_CRC_LEN; i++) {
			crc.update(0x00);
		}
	}

	/**
	 * Compares the DDM-data by the signature and part number.
	 * 
	 * @param obj the DDM-data to compare with
	 */
	@Override
	public int compareTo(DDMData obj) {
		
		// compare by signature
		int c = getSignature().compareTo(obj.getSignature());
		if (c != 0) return c;
		
		// compare by part number
		return Integer.compare(getPartNr(), obj.getPartNr());
	}
	
	@Override
	public String toString() {
		return getSignature();
	}
}
