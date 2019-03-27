package de.thkoeln.bibl.api.rfid.tag;

import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmBrmTableItem;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderConst;
import de.feig.FedmIscRssiItem;
import de.feig.FedmIsoTableItem;
import de.feig.FedmTableItem;
import de.feig.TagHandler.FedmIscTagHandler;

/**
 * Class implements an easy access to basic RFID tags. Communication is
 * based on the Feig IscTagHandler API or traditionally on the FedmTableItem
 * (ISO/BRM) implemented in the Feig API. The class supports ISO tags and
 * BRM tags. ISO tags are produced when using a reader in host mode, BRM tags 
 * when using a reader in the buffered reader mode.
 * 
 * Instances of this class and sub-classes implicitly support XML serializing
 * by JAXB.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
@XmlRootElement(name="base-tag")
@XmlAccessorType(XmlAccessType.NONE)
public class BaseTag implements Serializable, Comparable<BaseTag> {

	private static final long serialVersionUID = 1727302156973600877L;
	
	protected FedmIscTagHandler handler;
	
	@XmlAttribute (name = "uid", required = true)
	private String uid;
	
	@XmlAttribute (name = "type", required = true)
	private String name;
	
	@XmlElementWrapper (name = "rssi", required = false)
	private Map<Integer, Integer> rssi;
	
	/**
	 * Default constructor for serialization.
	 */
	protected BaseTag() {}
	
	/**
	 * Initialize a new BaseTag with the supplied tag handler.
	 * 
	 * @param handler the handler of the tag
	 * @throws FedmException if the Feig handler throws an exception 
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public BaseTag(FedmIscTagHandler handler) throws FedmException, 
			FePortDriverException, FeReaderDriverException {
		
		this.handler = handler;
		uid = handler.getUid();
		name = handler.getTagName();
		
		// try to get RSSI information
		try {rssi = getRSSI(handler.getRSSI());}
		catch (Exception e) {}
	}
	
	/**
	 * Initialize a new BaseTag with the supplied tag reader and BRM table tag.
	 * 
	 * @param reader the reader used for communication with the tag
	 * @param tag the tag as BRM table item
	 * @throws FedmException if the Feig handler throws an exception 
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public BaseTag(FedmIscReader reader, FedmBrmTableItem tag) throws FedmException, 
			FePortDriverException, FeReaderDriverException {
		
		uid = tag.getUid();
		name = getTagName(tag);
		
		// try to get RSSI information
		try {rssi = getRSSI(tag.getRSSI());}
		catch (Exception e) {}
	}
	
	/**
	 * Initialize a new BaseTag with the supplied tag reader and ISO table tag.
	 * 
	 * @param reader the reader used for communication with the tag
	 * @param tag the tag as ISO table item
	 * @throws FedmException if the Feig handler throws an exception 
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public BaseTag(FedmIscReader reader, FedmIsoTableItem tag) throws FedmException, 
			FePortDriverException, FeReaderDriverException {
		
		// create new tag handler
		this(new FedmIscTagHandler(reader, FedmIscTagHandler.TYPE_BASIC, tag));
	}
	
	/**
	 * Returns the UID of the tag.
	 * 
	 * @return the UID
	 */
	public String getUID() {
		return uid;
	}
	
	/**
	 * Returns the tag name (type), defined in the Feig API.
	 * 
	 * @return the tag name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the tag handler, used in the Feig API.
	 * 
	 * @return the tag handler
	 */
	public FedmIscTagHandler getHandler() {
		return handler;
	}
	
	/**
	 * Returns the tag handler class, defined in the Feig API.
	 * 
	 * @return the tag handler class
	 */
	@XmlElement (name = "handler", required = true)
	public Class<? extends FedmIscTagHandler> getHandlerClass() {
		return (handler != null) ? handler.getClass() : null;
	}
	
	/**
	 * Returns the RSSI (signal strength indicator) for this tag 
	 * from all antennas. The returned map contains the measured
	 * RSSI indexed by each available antenna.
	 * 
	 * @return the RSSI from all antennas or null if no RSSI is
	 * available
	 */
	public Map<Integer, Integer> getRSSI() {
		return rssi;
	}
	
	/**
	 * Returns the RSSI (signal strength indicator) for this tag 
	 * from a specific antenna.
	 * 
	 * @param antenna the antenna index
	 * @return the measured RSSI or a negative value if no RSSI is
	 * available
	 */
	public int getRSSI(int antenna) {
		return hasRSSI() ? rssi.get(antenna) : -1;
	}
	
	/**
	 * Returns the maximum RSSI (signal strength indicator) for this
	 * tag calculated over all available antennas.
	 * 
	 * @return the maximum RSSI or a negative value if no RSSI is
	 * available
	 */
	public int getMaxRSSI() {
		return hasRSSI() ? getMaxRSSI(rssi) : -1;
	}
	
	/**
	 * Checks if this tag supports RSSI (signal strength indicator).
	 * 
	 * @return true if the tag has a RSSI information
	 */
	public boolean hasRSSI() {
		return (rssi != null);
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
	
	/**
	 * Returns unique hash for this tag, calculated over the UID. 
	 * 
	 * @return hash code for this tag
	 */
	@Override
	public int hashCode() {
		return uid.hashCode();
	}

	/**
	 * Compares the tag by it's UID.
	 * 
	 * @param obj the tag to compare with
	 */
	@Override
	public int compareTo(BaseTag obj) {
		return uid.compareTo(obj.uid);
	}
	
	@Override
	public String toString() {
		return uid;
	}
	
	/**
	 * Returns tag type name for the specified tag.
	 * 
	 * @param tag the tag to get the type name from
	 * @return the tag type name
	 */
	private static String getTagName(FedmTableItem tag) {
		
		// check if data is valid
		if (!tag.isDataValid(FedmIscReaderConst.DATA_TRTYPE))
			return "NA";
		
		int type = tag.getIntegerData(FedmIscReaderConst.DATA_TRTYPE);
		
		// check all fields in FedmIscReaderConst
		for (Field field : FedmIscReaderConst.class.getDeclaredFields()) {
			
			int mod = field.getModifiers();
			
			// check for correct field attributes
			if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && 
				Modifier.isFinal(mod) && field.getType().equals(int.class) &&
				field.getName().startsWith("TR_TYPE")) {
				
				// check value of field
				try {					
					if (field.getInt(null) == type)
						// return field name
						return field.getName();
				}
				catch (Exception e) {}
			}
		}
		// field for specified id not found
		return Integer.toString(type);
	}
	
	/**
	 * Returns the RSSI map from the specified tag.
	 * 
	 * @param tag the tag to get the RSSI from
	 * @return the RSSI map or null if no RSSI is available
	 * @throws TagException if the reading of the RSSI failed
	 */
	public static Map<Integer, Integer> getRSSI(FedmTableItem tag) 
			throws TagException {
		
		HashMap<Integer, FedmIscRssiItem> tagRSSI;
		
		// read RSSI info
		try {
			tagRSSI = tag.getRSSI();
		}
		catch (Exception e) {
			throw new TagException("could not read RSSI", e);
		}
		
		// no RSSI available
		if (tagRSSI == null) return null;

		// convert RSSI table to fixed form
		Map<Integer, Integer> fixedRSSI = getRSSI(tagRSSI);

		return fixedRSSI;
	}
	
	/**
	 * Returns the RSSI map as simple number representation in numeric 
	 * dbm <ant-idx, RSSI-value>.
	 * 
	 * @param map the original hash map based on FedmIscRssiItem
	 * @return the simple representation in numeric dbm
	 */
	private static Map<Integer, Integer> getRSSI(Map<Integer, FedmIscRssiItem> map) {
		
		// create new HashMap representation of RSSI
		Map<Integer, Integer> ret = new HashMap<>(map.size());
		
		// loop through RSSI values
		for (Map.Entry<Integer, FedmIscRssiItem> elm : map.entrySet()) {
			
			// add correct integer version of RSSI to map
			ret.put(elm.getKey(), elm.getValue().RSSI & 0xFF);
		}
		
		return ret;
	}
	
	/**
	 * Returns the maximum RSSI over all antennas from the specified 
	 * RSSI Map.
	 * 
	 * @param rssi the RSSI map
	 * @return the maximum RSSI value
	 */
	private static int getMaxRSSI(Map<Integer, Integer> rssi) {
		
		int maxRSSI = 0;
		
		// find biggest RSSI
		for (Entry<Integer, Integer> elm : rssi.entrySet()) {
			
			int val = elm.getValue();
			if (maxRSSI < val) maxRSSI = val;
		}

		return maxRSSI;
	}
	
	/**
	 * Returns the maximum RSSI over all antennas from the specified
	 * tag.
	 * 
	 * @param tag the tag to get RSSI from
	 * @return the maximum RSSI or negative value if no RSSI available
	 * @throws TagException if the reading of the RSSI failed
	 */
	public static int getMaxRSSI(FedmTableItem tag) throws TagException {
		
		Map<Integer, Integer> rssi = getRSSI(tag);
		
		if (rssi == null) return -1;

		return getMaxRSSI(rssi);
	}
}
