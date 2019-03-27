package de.thkoeln.bibl.api.lms.slnp.message;

import java.util.regex.PatternSyntaxException;

/**
 * Implements a SLNP response message. The class describes the basic format
 * of a response message.
 * 
 * A response message has the following format:
 * {@code CODE [Key:]Value}
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class ResponseMessage extends Message {
	
	/** REGEX defining the message structure */
	private static final String	
		MSG_REGX = "^(?<code>[0-9]{3})\\s*((?<key>.*):)?(?<val>.*)$";

	protected MessageCode code;
	protected String value;
	private String key;

	/**
	 * Defines message types and the range of status codes for each
	 * message class.
	 */
	public static enum MessageType {
		
		/** 2xx: succeed status codes */
		SUCCEED(200, 299),
		
		/** 5xx: failed status codes */
		FAILED(500, 599),
		
		/** 6xx: data response codes */
		DATA(600, 699),
		
		UNKNOWN(0, -1);
		
		private int min;
		private int max;
		
		private MessageType(int min, int max) {
			this.min = min;
			this.max = max;
		}
		
		/**
		 * Checks if the MessageCode is part of the MessageType.
		 *  
		 * @param code the MessageCode to check
		 * @return true if the MessageCode is part of the MessageType
		 */
		private boolean includesCode(MessageCode code) {
			return (code.number >= min && code.number <= max);
		}
		
		/**
		 * Returns the MessageType for the supplied MessageCode.
		 * 
		 * @param code the MessageCode to lookup the MessageType for
		 * @return the MessageType for the supplied MessageCode
		 */
		private static MessageType getType(MessageCode code) {
			for(MessageType t : MessageType.values()) {
				if (t.includesCode(code)) return t;
			}
			return UNKNOWN;
		}
	}
	
	/**
	 * Defines message codes.
	 */
	public static enum MessageCode {
		
		/** server message */
		SUCCEED_MSG(220),
		
		/** command succeed with okay message */
		SUCCEED_OK(240),
		
		/** mark end of data response */
		SUCCEED_EOD(250),
		
		/** application error */
		FAILED_APP(510),
		
		/** database error */
		FAILED_DB(511),
		
		/** allocation error */
		FAILED_ALLOC(512),
		
		/** syntax error */
		FAILED_SYNTAX(520),
		
		/** retransmission of command precedes each response with data */
		DATA_COMMAND(600),
		
		/** part of a data unit within a response of 1 single data unit */
		DATA_SINGLE(601),
		
		/** separator of data units within a response of multiple data units */
		DATA_SEPARATOR(602),
		
		/** part of a data unit within a response of multiple data units */
		DATA_MULTI(603),
		
		UNKNOWN(-1);
		
		public int number;
		public MessageType type;
		
		private MessageCode(int number) {
			this.number = number;
			this.type = MessageType.getType(this);
		}
		
		/**
		 * Returns a message code enum from a code number.
		 * 
		 * @param number the message code number
		 * @return message code enum representation
		 */
		public static MessageCode getCode(int number) {
			for(MessageCode c : MessageCode.values()) {
				if (c.number == number) return c;
			}
			return UNKNOWN;
		}
	}
	
	/**
	 * Initialize a ResponseMessage with a raw SLNP message and a pattern, 
	 * which defines the message format with matching groups.
	 * 
	 * @param msg the raw SLNP server message
	 * @param pattern the format as REGEX string
	 * @throws PatternSyntaxException if the pattern's syntax is invalid
	 * @throws MessageFormatException if the message has a invalid format
	 */
	public ResponseMessage(String msg, String pattern) throws PatternSyntaxException,
		MessageFormatException {
		
		super(msg, pattern);
		
		// parse the message
		parse();
		extractData();
	}
	
	/**
	 * Initialize a ResponseMessage with a raw SLNP server message.
	 * 
	 * @param msg the raw server message
	 * @throws MessageFormatException if the message has a invalid format
	 */
	public ResponseMessage(String msg) throws MessageFormatException {
		this(msg, MSG_REGX);
	}
	
	/**
	 * Extracts the data from the parsed message. Sub-classes must override 
	 * this method and implement the logic for their specific message format.
	 */
	protected void extractData() {
		code = MessageCode.getCode(Integer.parseInt(getMatch("code")));
		key = getMatch("key");
		value = getMatch("val");
	}
	
	/**
	 * Returns the message code.
	 * 
	 * @return the message code
	 */
	public MessageCode getCode() {
		return code;
	}
	
	/**
	 * Returns the message key.
	 * 
	 * @return the message key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Returns the message value.
	 * 
	 * @return the message value
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
	 * Returns the command if the message is a command type.
	 * 
	 * @return the command or null if the message is't a command type
	 */
	public String getCommand() {
		return isCommand() ? value : null;
	}
	
	/**
	 * Checks if the message is a data command.
	 * 
	 * @return true if the message is a data command
	 */
	public boolean isCommand() {
		return (code == MessageCode.DATA_COMMAND);
	}
	
	/**
	 * Checks if the message is a error type.
	 * 
	 * @return true if the message is a error type
	 */
	public boolean isError() {
		return (code.type == MessageType.FAILED || 
				code.type == MessageType.UNKNOWN);
	}
	
	/**
	 * Checks if the message is a data type.
	 * 
	 * @return true if the message is a data type
	 */
	public boolean isData() {
		return (code.type == MessageType.DATA);
	}

	/**
	 * Checks if the message is a end marker.
	 * 
	 * @return true if the message is a end marker
	 */
	public boolean isEndMarker() {
		return (code.type == MessageType.FAILED ||
				code.type == MessageType.SUCCEED ||
				code == MessageCode.SUCCEED_EOD);
	}
}
