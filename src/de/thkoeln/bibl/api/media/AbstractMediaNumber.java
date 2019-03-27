package de.thkoeln.bibl.api.media;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class implements the structure of a media number. Specializing
 * sub-classes must describe the concrete media number with an REGEX
 * and can use the functionality of this class to access the number
 * in an objective way.
 * This class and all sub-classes supports the serializing to/from XML data.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
@XmlRootElement(name="default-media-number")
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractMediaNumber implements Serializable, 
		Comparable<AbstractMediaNumber> {
	
	/** The media number REGEX group type and it's default value */
	public static enum GroupType {
		
		STRING(""),
		
		INTEGER("0");
		
		private String defValue;
		
		private GroupType(String defValue) {
			this.defValue = defValue;
		}
		
		public String getDefValue() {
			return defValue;
		}
	};
	
	private static final long serialVersionUID = -1562792080603986449L;
	
	@XmlElement (name = "number", required = true)
	private String number;
	
	private Matcher matcher;
	private Map<String, GroupType> groupDef;
	
	/**
	 * Default constructor for serialization.
	 */
	protected AbstractMediaNumber() {} 
		
	/**
	 * Initialize a new AbstractMediaNumber with the supplied REGEX
	 * pattern and group definition.
	 * 
	 * @param number the media number
	 * @param pattern the pattern defining the media number structure
	 * @param groupDef the group definition describing the pattern groups
	 * @throws MediaFormatException if the supplied media number could't
	 * parsed with the supplied pattern
	 */
	public AbstractMediaNumber(String number, Pattern pattern, 
			Map<String, GroupType> groupDef) throws MediaFormatException {
		
		this.number = number.trim();
		this.groupDef = groupDef;

		// match against number
		matcher = pattern.matcher(number);
		
		// throw exception if number don't match
		if (!matcher.matches()) 
			throw new MediaFormatException("invalid media number format");
	}
	
	/**
	 * Initialize a new AbstractMediaNumber with the supplied REGEX
	 * String and group definition.
	 * 
	 * @param number the media number
	 * @param regex the pattern String defining the media number structure
	 * @param groupDef the group definition describing the pattern groups
	 * @throws PatternSyntaxException if the supplied REGEX pattern is invalid
	 * @throws MediaFormatException if the supplied media number could't
	 * parsed with the supplied pattern
	 */
	public AbstractMediaNumber(String number, String regex, 
			Map<String, GroupType> groupDef) throws PatternSyntaxException, 
			MediaFormatException {
		
		this(number, Pattern.compile(regex), groupDef);	
	}
	
	/**
	 * Initialize a new AbstractMediaNumber. The media number pattern
	 * and group definition is defined in the specializing sub class.
	 * 
	 * @param number the media number
	 * @throws MediaFormatException if the supplied media number could't
	 * parsed
	 */
	public AbstractMediaNumber(String number) 
			throws MediaFormatException {
		
		this.number = number.trim();
		this.groupDef = getGroupDefinition();
		
		// match against number
		matcher = getPattern().matcher(number);
		
		// throw exception if number don't match
		if (!matcher.matches()) 
			throw new MediaFormatException("invalid media number format");
	}
	
	/**
	 * Returns the media number in String representation.
	 * 
	 * @return the media number
	 */
	public String getNumber() {
		return number;
	}
	
	/**
	 * Returns the value from a matched group by group-name.
	 * 
	 * @param regexGroup the name of the matching group
	 * @return the matched data
	 * @throws IllegalArgumentException if there is no capturing group 
	 * in the pattern with the given name
	 */
	public String getGroup(String regexGroup) 
			throws IllegalArgumentException {
		
		String val = matcher.group(regexGroup);
		return (val == null) ? val : val.trim();
	}
	
	/**
	 * Returns the value from a matched group by group-index.
	 * 
	 * @param regexGroup the index of the matching group
	 * @return the matched data
	 * @throws IndexOutOfBoundsException if there is no capturing group 
	 * in the pattern with the given index
	 */
	public String getGroup(int regexGroup) 
			throws IndexOutOfBoundsException {
		
		String val = matcher.group(regexGroup);
		return (val == null) ? val : val.trim();
	}
	
	/**
	 * Returns the value from a matched group by group-name as integer.
	 * 
	 * @param regexGroup the name of the matching group
	 * @return the matched data or 0 when group has no value
	 * @throws IllegalArgumentException If there is no capturing group 
	 * in the pattern with the given name
	 */
	public int getGroupInteger(String regexGroup) 
			throws IllegalArgumentException {
		
		return Integer.parseInt(
				getSafeValue(regexGroup, GroupType.INTEGER));
	}
	
	/**
	 * Returns the value from a matched group by group-index as integer.
	 * 
	 * @param regexGroup the index of the matching group
	 * @return the matched data or 0 when group has no value
	 * @throws IndexOutOfBoundsException if there is no capturing group 
	 * in the pattern with the given index
	 */
	public int getGroupInteger(int regexGroup) 
			throws IndexOutOfBoundsException {
		
		return Integer.parseInt(
				getSafeValue(regexGroup, GroupType.INTEGER));
	}
	
	/**
	 * Returns the default REGEX group definition. The definition 
	 * corresponds to the default pattern returned by getDefPattern().
	 * The key defines the group, GroupType the way to interpret the value
	 * and the map order the weight of the groups. First element will be 
	 * compared first and so on.
	 * 
	 * @return the default group definition
	 */
	public abstract LinkedHashMap<String, GroupType> getGroupDefinition();
	
	/**
	 * Returns the default REGEX pattern. The pattern corresponds to
	 * the default group definition returned by getDefGroupDef() and
	 * describes the format of the media number.
	 * 
	 * @return the default pattern
	 */
	public abstract Pattern getPattern();
	
	@Override
	public String toString() {
		return number;
	}

	/**
	 * Compare the media number by the group definition order.
	 * 
	 * @param o the media number to compare with
	 */
	@Override
	public int compareTo(AbstractMediaNumber o) {
		
		for (Entry<String, GroupType> def : groupDef.entrySet()) {
			String group = def.getKey();
			int cmp = compare(getGroup(group), o.getGroup(group), def.getValue());
			
			if (cmp != 0) return cmp;
		}
		
		return 0;
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
	 * Compares two values depending on the specified type.
	 * If a null reference is passed as value, the default value
	 * is automatically used depending on the type. The Return value is
	 * equivalent to the compareTo method of the type specific compare
	 * operation.
	 * 
	 * @param val1 first value
	 * @param val2 second value
	 * @param type compare type
	 * @return positive, negative or zero value
	 * @throws NumberFormatException if a value coul't parsed
	 */
	private int compare(String val1, String val2, GroupType type) 
			throws NumberFormatException {
		
		// return equal if both groups are undefined
		if (val1 == null && val2 == null) return 0;
		
		// set default value if value is undefined
		if (val1 == null) val1 = type.getDefValue();
		if (val2 == null) val2 = type.getDefValue();
		
		// compare values depending on type
		switch (type) {
			// compare as integer
			case INTEGER:
				return Integer.valueOf(val1)
						.compareTo(Integer.valueOf(val2));
			// compare alpha numerical
			case STRING:
			default:
				return val1.compareTo(val2);
		}
	}
	
	/**
	 * Returns the value from a matched group by group-name 
	 * or the groups default value if the group has no value.
	 * 
	 * @param regexGroup the name of the matching group
	 * @param type the type of the group
	 * @return the matched data or the corresponding default value
	 * @throws IllegalArgumentException if there is no capturing group
	 * in the pattern with the given name
	 */
	private String getSafeValue(String regexGroup, GroupType type) 
			throws IllegalArgumentException {
		
		String val = getGroup(regexGroup);
		return (val == null) ? type.getDefValue() : val;
	}
	
	/**
	 * Returns the value from a matched group by group-index 
	 * or the groups default value if the group has no value.
	 * 
	 * @param regexGroup the index of the matching group
	 * @param type the type of the group
	 * @return the matched data or the corresponding default value
	 * @throws IndexOutOfBoundsException if there is no capturing group
	 * in the pattern with the given index
	 */
	private String getSafeValue(int regexGroup, GroupType type) 
			throws IndexOutOfBoundsException {
		
		String val = getGroup(regexGroup);
		return (val == null) ? type.getDefValue() : val;
	}
}
