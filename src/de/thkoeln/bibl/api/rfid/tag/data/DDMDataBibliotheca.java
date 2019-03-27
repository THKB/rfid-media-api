package de.thkoeln.bibl.api.rfid.tag.data;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class implements Bibliotheca's non standard implementation of the DDM
 * and supports Bibliotheca's specific extended tag data.
 * 
 * Bibliotheca's data block starts at index 32 with two leading zero bytes.
 * 
 * The data layout in hex:
 * 
 * D..D 0000 LL UUUU CC TT SS..SS VVVVVVVV
 * 
 * D = default DDM data					32 bytes
 * 0 = leading zero bytes				2 bytes
 * L = data length, including T,S,V		1 byte
 * U = unknown data						2 bytes
 * C = checksum, XOR over L,U,T,S		1 byte
 * T = media type						1 byte
 * S = signature						69 bytes
 * V = validation code, optional 'W_OK'	4 bytes
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
@XmlRootElement(name="ddm-data-bibliotheca")
@XmlAccessorType(XmlAccessType.NONE)
public class DDMDataBibliotheca extends DDMData {

	private static final long serialVersionUID = 8067771700680237272L;
	
	private static final int DDM_EXT_SIZE = DDM_SIZE + 80;
	private static final int DDM_EXT_LEN_IDX = DDM_SIZE + 2;
	private static final int DDM_EXT_CHECKSUM_IDX = DDM_SIZE + 5;
	private static final int DDM_EXT_MEDIATYPE_IDX = DDM_SIZE + 6;
	private static final int DDM_EXT_MEDIATYPE_LEN = 1;
	private static final int DDM_EXT_SIGNATURE_IDX = DDM_SIZE + 7;
	private static final int DDM_EXT_SIGNATURE_LEN = 69;
	private static final int DDM_EXT_VALID_LEN = 4;
	
	@XmlElement (name = "crc-ext", required = true)
	private int checksum;
	
	/**
	 * Initialize new DDMDataBibliotheca with the specified data array.
	 * 
	 * @param data the data to initialize the data-model with
	 */
	public DDMDataBibliotheca(byte[] data) {
		// initialize DDM data
		super(data, DDM_EXT_SIZE, "DDM Bibliotheca");
	}
	
	/**
	 * Initialize new DDMDataBibliotheca with an empty data-model.
	 */
	public DDMDataBibliotheca() {
		this(new byte[DDM_EXT_SIZE]);
	}
	
	/**
	 * Initialize new DDMDataBibliotheca with an hex encoded 
	 * String representing the data.
	 * 
	 * @param hex the hex encoded String representing the data
	 */
	public DDMDataBibliotheca(String hex) {
		this(DatatypeConverter.parseHexBinary(hex));
	}
	
	/**
	 * Initialize new DDMDataBibliotheca with the tag-data from the
	 * supplied TagData.
	 * 
	 * @param data the TagData holding the data for this 
	 * data-model
	 */
	public DDMDataBibliotheca(TagData data) {
		this(data.getData());
	}
	
	/**
	 * Returns the type.
	 * 
	 * @return the type
	 */
	@XmlElement (name = "type", required = true)
	public int getType() {
		return getData(DDM_EXT_MEDIATYPE_IDX);
	}
	
	@Override
	public int getVersion() {
		// version and status byte are swapped
		return super.getStatus();
	}
	
	@Override
	public int getStatus() {
		// version and status byte are swapped
		return super.getVersion();
	}

	@Override
	public String getSignature() {
		// get signature from default DDM location 
		String sig = super.getSignature();
		
		// check if default location contained signature
		if (!sig.isEmpty()) return sig;
			
		// get signature from extended data
		return getDataRange(DDM_EXT_SIGNATURE_IDX, getSigLength());
	}
	
	@Override
	public boolean isValid() {		
		return (super.isValid() && 
			   (checksum == getChecksum()));
	}
	
	/**
	 * Return the extended XOR checksum.
	 * 
	 * @return the checksum
	 */
	public int getCRCExt() {
		return checksum;
	}
	
	@Override
	public void setData(byte[] data) {
		setData(data, DDM_EXT_SIZE);
		updateChecksum();
	}
	
	/**
	 * Updates the CRC-16 checksum of the DDM-data and the
	 * extended XOR checksum in the Bibliotheca data-model.
	 */
	@Override
	protected void updateChecksum() {
		// update checksum in DDM data
		super.updateChecksum();
		
		// build checksum for extended data
		checksum = 0x00;
		int startIdx = DDM_EXT_LEN_IDX;
		int endIdx = DDM_EXT_SIGNATURE_IDX + getSigLength();
		
		// XOR over: data length, unknown data, media type and signature
		for (int i = startIdx; i < endIdx; i++) {
			// skip checksum byte
			if (i == DDM_EXT_CHECKSUM_IDX) continue;
			checksum ^= data[i];
		}
	}
	
	/**
	 * Returns the signature length.
	 * 
	 * @return the signature length
	 */
	private int getSigLength() {
		// get length information from extended data
		int len = getData(DDM_EXT_LEN_IDX) - DDM_EXT_MEDIATYPE_LEN 
				- DDM_EXT_VALID_LEN;
		// validation check, limit length to a minimum
		if (len < 0) len = 0;
		// validation check, limit length to a maximum
		if (len > DDM_EXT_SIGNATURE_LEN) len = DDM_EXT_SIGNATURE_LEN;
		return len; 
	}
	
	/**
	 * Returns the extended XOR checksum.
	 * 
	 * @return the checksum
	 */
	private int getChecksum() {
		return getData(DDM_EXT_CHECKSUM_IDX);
	}
}
