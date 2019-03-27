package de.thkoeln.bibl.api.lms.slnp.message;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Class implements a generic abstract SLNP protocol message and 
 * methods to validate a message against protocol patterns.
 * 
 * Sub-classes must define the specific SLNP message format with a
 * REGEX describing a valid message and defining matching groups to
 * extract message data from the raw message String.
 * 
 * Example:
 * {@code ^(?<code>[0-9]{3})(?<name>.+)$}
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public abstract class Message {
	
	private static final char MSG_END = '\n';
	
	private String msg;
	private Pattern msgPtrn;
	private Matcher msgMatcher;
	private boolean isValid;
	
	/**
	 * Initialize a Message with a raw SLNP message and a pattern, 
	 * which defines the message format with matching groups.
	 * 
	 * @param msg the raw SLNP server message
	 * @param pattern the format as REGEX string
	 * @throws PatternSyntaxException if the pattern's syntax is invalid
	 */
	public Message(String msg, String pattern) throws PatternSyntaxException {
		
		this.msg = msg.trim();
		this.msgPtrn = Pattern.compile(pattern);
		this.msgMatcher = msgPtrn.matcher(msg);
		this.isValid = false;
	}
	
	/**
	 * Returns the raw message.
	 * 
	 * @return the message
	 */
	public String getMessage() {
		return msg;
	}
	
	/**
	 * Returns the message including the SLNP line termination.
	 * 
	 * @return the message with line termination
	 */
	public String getSLNPMessage() {
		return getMessage() + MSG_END;
	}
	
	/**
	 * Returns the value from a matched group by group-name.
	 * 
	 * @param group the name of the matching group
	 * @return the matched data
	 * @throws IllegalArgumentException if there is no capturing group 
	 * in the pattern with the given name
	 */
	public String getMatch(String group) throws IllegalArgumentException {
		
		String match = msgMatcher.group(group);
		
		if (match != null) return match.trim();
		return match;
	}
	
	/**
	 * Returns the value from a matched group by group-index.
	 * 
	 * @param group the index of the matching group
	 * @return the matched data
	 * @throws IndexOutOfBoundsException if there is no capturing group 
	 * in the pattern with the given index
	 */
	public String getMatch(int group) throws IndexOutOfBoundsException {
		
		String match = msgMatcher.group(group);
		
		if (match != null) return match.trim();
		return match;
	}
	
	/**
	 * Checks the Message against the defined REGEX.
	 * 
	 * @return true if the message is valid
	 */
	public boolean isValid() {
		return isValid;
	}
	
	/**
	 * Parse the raw message string against the defined REGEX.
	 * After the message was parsed, the data is accessible with the
	 * getMatch() methods.
	 * 
	 * @return a matcher for the parsed message
	 * @throws MessageFormatException if the raw message has a invalid format
	 */
	public Matcher parse() throws MessageFormatException {
		
		// try to match the message
		if (!msgMatcher.matches()) 
			throw new MessageFormatException(
					String.format("illegal message format: '%s'", msg));
		
		isValid = true;
		return msgMatcher;
	}
	
	@Override
	public String toString() {
		return msg;
	}
}
