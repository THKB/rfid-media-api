package de.thkoeln.bibl.api.rfid.tag;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmBrmTableItem;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderConst;
import de.feig.FedmIsoTableItem;
import de.feig.TagHandler.FedmIscTagHandler;
import de.feig.TagHandler.FedmIscTagHandler_ISO15693;
import de.feig.TagHandler.FedmIscTagHandler_ISO15693_TagInfoResult;
import de.feig.TagHandler.FedmIscTagHandler_Result;

/**
 * Class extends the base RFID tag with ISO 15693 features. Communication 
 * is based on the Feig IscTagHandler API.
 * 
 * Instances of this class support XML serializing by JAXB.
 * 
 * On object initialization all tag information and data will be read 
 * from the tag and stored for later offline access.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
@XmlRootElement(name="iso15693-tag")
@XmlAccessorType(XmlAccessType.NONE)
public class ISO15693Tag extends BaseTag {

	private static final long serialVersionUID = -1921825258171931129L;
	
	public static final int AFI_NA = -1;

	@XmlElement (name = "afi", required = true)
	private int afi;
	
	@XmlElement (name = "block-size", required = true)
	private int blockSize;
	
	@XmlElement (name = "sys-size", required = true)
	private int sysSize;
	
	@XmlElement (name = "mem-size", required = true)
	private int memSize;
	
	@XmlElement (name = "payload", required = true)
	private byte[] data;
	
	/**
	 * Default constructor for serialization.
	 */
	protected ISO15693Tag() {}
	
	/**
	 * Initialize a new ISO15693Tag with the supplied tag handler.
	 * 
	 * @param handler the handler of the tag
	 * @throws TagIOException if the communication with the tag failed
	 * @throws FedmException if the Feig handler throws an exception
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public ISO15693Tag(FedmIscTagHandler_ISO15693 handler) throws TagIOException, 
			FedmException, FePortDriverException, FeReaderDriverException  {
		
		super(handler);
		
		// read tag system information
		FedmIscTagHandler_ISO15693_TagInfoResult info = getSystemInfo(handler);
		// convert AFI back to unsigned
		afi = info.AFI & 0xFF;
		
		// get memory info
		memSize = info.memSize & 0x00FF;
		sysSize = (info.memSize & 0xFF00) >> 8;
		
		// set blockSize
		blockSize = handler.getTabItem().blockSize;
		
		// read tag payload
		data = getPayload(handler, 0x00, memSize, false).data;
	}
	
	/**
	 * Initialize a new ISO15693Tag with the supplied tag reader and BRM table tag.
	 * 
	 * @param reader the reader used for communication with the tag
	 * @param tag the tag as BRM table item
	 * @throws TagIOException if the communication with the tag failed
	 * @throws FedmException if the Feig handler throws an exception 
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public ISO15693Tag(FedmIscReader reader, FedmBrmTableItem tag) throws TagIOException, 
			FedmException, FePortDriverException, FeReaderDriverException {
		
		super(reader, tag);
		
		// not supported in BRM
		sysSize = 0x00;
		
		int blockCount = tag.getBlockCount();
		
		blockSize = tag.blockSize;
		memSize = blockCount * blockSize;
		
		afi = getAFI(tag);
		data = getPayload(tag, tag.getBlockAddress(), blockCount);
	}
	
	/**
	 * Initialize a new ISO15693Tag with the supplied tag reader and ISO table tag.
	 * 
	 * @param reader the reader used for communication with the tag
	 * @param tag the tag as ISO table item
	 * @throws TagIOException if the communication with the tag failed
	 * @throws FedmException if the Feig handler throws an exception 
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public ISO15693Tag(FedmIscReader reader, FedmIsoTableItem tag) throws TagIOException, 
			FedmException, FePortDriverException, FeReaderDriverException  {
		
		// create new tag handler
		this(new FedmIscTagHandler_ISO15693(reader, tag));
	}
	
	/**
	 * Returns the tag data (payload) as byte array.
	 * 
	 * @return the tag data
	 */
	public byte[] getData() {
		return data;
	}
	
	/**
	 * Returns the AFI of the tag.
	 * 
	 * @return the AFI value
	 */
	public int getAFI() {
		return afi;
	}
	
	/**
	 * Checks if the tag supports the AFI.
	 * 
	 * @return true if tag supports the AFI
	 */
	public boolean supportsAFI() {
		return getAFI() != AFI_NA;
	}
	
	/**
	 * Returns the memory size in blocks of the tag.
	 * 
	 * @return the memory size in blocks
	 */
	public int getMemSize() {
		return memSize;
	}
	
	/**
	 * Returns the system memory size in blocks the tag uses.
	 * 
	 * @return the system memory size
	 */
	public int getSysSize() {
		return sysSize;
	}
	
	/**
	 * Returns the block size in bytes of the tag.
	 * 
	 * @return the block size in bytes
	 */
	public int getBlockSize() {
		return blockSize;
	}
	
	/**
	 * Reads the tag data (payload) with the optional security status information
	 * from the supplied handler.
	 * 
	 * @param handler the handler of the tag
	 * @param firstBlock the first block to read the data from
	 * @param blockCount the number of blocks to read
	 * @param withSecStat true to include the security status
	 * @return the read data as FedmIscTagHandler_Result
	 * @throws TagIOException if the communication with the tag failed
	 * @throws FedmException if the Feig handler throws an exception
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	private FedmIscTagHandler_Result getPayload(FedmIscTagHandler handler, int firstBlock, 
			int blockCount, boolean withSecStat) throws FedmException, FePortDriverException, 
			FeReaderDriverException, TagIOException {
		
		FedmIscTagHandler_Result res = new FedmIscTagHandler_Result();
		
		int re = 0;
		// read multiple blocks with optional security info
		if (withSecStat)
			re = handler.readMultipleBlocksWithSecStatus(firstBlock, blockCount, res);
		else
			re = handler.readMultipleBlocks(firstBlock, blockCount, res);
		// check if error occurred
		if (re != 0) throw new TagIOException("error reading tag user data");
		return res;
	}
	
	/**
	 * Reads the tag data (payload) from the supplied BRM tag.
	 * 
	 * @param tag tag to get data from
	 * @param firstBlock the first block to read the data from
	 * @param blockCount he number of blocks to read
	 * @return the data or null if the data could'n be read
	 */
	private byte[] getPayload(FedmBrmTableItem tag, int firstBlock, int blockCount) {
		
		// check if data is valid
		if (!tag.isDataValid(FedmIscReaderConst.DATA_RxDB)) return null;
		return tag.getByteArrayData(FedmIscReaderConst.DATA_RxDB, firstBlock, blockCount);
	}
	
	/**
	 *  Reads the tag system information.
	 *  
	 * @param handler the handler of the tag
	 * @return the system information as FedmIscTagHandler_ISO15693_TagInfoResult
	 * @throws TagIOException if the communication with the tag failed
	 * @throws FedmException if the Feig handler throws an exception
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	private FedmIscTagHandler_ISO15693_TagInfoResult getSystemInfo(FedmIscTagHandler_ISO15693 handler) 
			throws FePortDriverException, FeReaderDriverException, FedmException, TagIOException  {
		
		FedmIscTagHandler_ISO15693_TagInfoResult info = new FedmIscTagHandler_ISO15693_TagInfoResult();
		// read the information
		int re = handler.getSystemInformation(info);
		// check if error occurred
		if (re != 0) throw new TagIOException("error reading tag system information");
		return info;
	}
	
	/**
	 * Reads the AFI value from the tag.
	 * 
	 * @param tag the tag to get AFI value from
	 * @return the AFI value or AFI_NA if not supported
	 */
	private int getAFI(FedmBrmTableItem tag) {
		
		// check if data is valid
		if (tag.isDataValid(FedmIscReaderConst.DATA_AFI))
			return tag.getIntegerData(FedmIscReaderConst.DATA_AFI);
		
		// AFI value not supported
		return AFI_NA;
	}
}
