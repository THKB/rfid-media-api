package de.thkoeln.bibl.api.lms.slnp.message;

import java.util.regex.PatternSyntaxException;

/**
 * Implements a SLNP request message. The class describes the format
 * of the request message.
 * 
 * A request message has the following format:
 * {@code [Key:]Value}
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class RequestMessage extends Message {
	
	private static final char MSG_DATA_SEPERATOR = ':';
	
	/** REGEX defining the message structure */
	private static final String 
		MSG_REGX = "^((?<key>.*):)?(?<val>.*)$";

	private String key;
	private String value;

	/**
	 * Initialize a RequestMessage with a raw SLNP server message.
	 * 
	 * @param msg the raw server message
	 * @throws PatternSyntaxException if the pattern's syntax is invalid
	 * @throws MessageFormatException if the supplied message has an invalid format
	 */
	public RequestMessage(String msg) throws PatternSyntaxException, 
			MessageFormatException {
		
		// initialize message
		super(msg, MSG_REGX);
		
		// parse the message
		parse();
		extractData();
	}
	
	/**
	 * Initialize a RequestMessage with a key and value.
	 * 
	 * @param key the key
	 * @param value the value
	 * @throws PatternSyntaxException if the pattern's syntax is invalid
	 * @throws MessageFormatException if the created message has an invalid format
	 */
	public RequestMessage(String key, String value) throws PatternSyntaxException, 
			MessageFormatException {
		
		this(key + MSG_DATA_SEPERATOR + value);
	}
	
	/**
	 * Extracts the data from the parsed message. Sub-classes must override 
	 * this method and implement the logic for their specific message format.
	 */
	protected void extractData() {
		key = getMatch("key");
		value = getMatch("val");
	}
	
	/**
	 * Returns the message key.
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Returns the message value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Checks if the message has a key.
	 * 
	 * @return true if the message has a key
	 */
	public boolean hasKey() {
		return (key != null);
	}
	
	/**
	 * Checks if the message has a value.
	 * 
	 * @return true if the message has a value
	 */
	public boolean hasValue() {
		return (value != null);
	}
}
