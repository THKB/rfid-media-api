package de.thkoeln.bibl.api.lms.slnp.message;

/**
 * Class implements SLNP connect response message.
 * 
 * A connect response message has the following format:
 * 
 * {@code CODE SLNP s..s@Version: s..s@pid: n..n@charset:s..s@ssl:s..s}
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class ConnectResponseMessage extends ResponseMessage {
	
	private String name;
	private String version;
	private String pid;
	private String charset;
	private boolean ssl;
	
	/** REGEX defining the message structure */
	private static final String 
		MSG_REGX = "^(?<code>[0-9]{3})\\s*SLNP\\s*"
				+ "(?<val>"
					+ "(?<name>.+)"
					+ "@Version:(?<ver>.+)"
					+ "@pid:(?<pid>[0-9]+)"
					+ "@charset:(?<char>.+)"
					+ "@ssl:(?<ssl>.+)"
				+ ")$";
	
	/**
	 * Initialize a ConnectResponseMessage with the supplied 
	 * raw message string received from the server.
	 * 
	 * @param msg the raw message string
	 * @throws MessageFormatException if the message string has a invalid format
	 */
	public ConnectResponseMessage(String msg) throws MessageFormatException {
		super(msg, MSG_REGX);
	}
	
	@Override
	protected void extractData() {
		
		// basic message implementation
		code = MessageCode.getCode(Integer.parseInt(getMatch("code")));
		value = getMatch("val");
		
		// local message implementation
		name = getMatch("name");
		version = getMatch("ver");
		pid = getMatch("pid");
		charset = getMatch("char");
		ssl = Boolean.parseBoolean(getMatch("ssl"));
	}
	
	/**
	 * Returns the SLNP server name.
	 * 
	 * @return the server name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the SLNP server version.
	 * 
	 * @return the server version
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Returns the process ID of the SLNP server severing 
	 * the connected client.
	 * 
	 * @return the process ID
	 */
	public String getPID() {
		return pid;
	}
	
	/**
	 * Returns the charset the server encodes the data 
	 * when communicating with the client.
	 * 
	 * @return the charset name representation
	 */
	public String getCharset() {
		return charset;
	}
	
	/**
	 * Checks if the connection is secured with SSL.
	 * 
	 * @return true if the connection is using SSL.
	 */
	public boolean isSSL() {
		return ssl;
	}
}
