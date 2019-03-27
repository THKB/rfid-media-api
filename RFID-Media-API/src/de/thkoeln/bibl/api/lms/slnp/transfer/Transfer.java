package de.thkoeln.bibl.api.lms.slnp.transfer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.slnp.message.Message;

/**
 * Implements the basic class which describes all SLNP transfer 
 * related for handling the messages. A Transfer can be used 
 * for receiving and sending messages. All messages are internally 
 * stored in a buffer.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * @param <T> the type of message the transfer is using
 */
public abstract class Transfer<T extends Message> {
	
	private Connectable con;
	private BufferedReader in;
	private PrintWriter out;
	private Charset encoding;
	private Vector<T> messages;
	
	/**
	 * Initialize a new Transfer with an object that implements the  
	 * Connectable interface. The supplied encoding is used for the
	 * communication with the SLNP server.
	 * 
	 * @see java.nio.charset.Charset
	 * 
	 * @param con a reference to a Connectable object. This reference will
	 * be used to communicate with the server
	 * @param encoding the data encoding
	 */
	public Transfer(Connectable con, String encoding) {
		
		this.con = con;
		this.encoding = Charset.forName(encoding);
		
		messages = new Vector<>();
		
		// create streams
		setStreams();
	}
	
	/**
	 * Initialize a new Transfer with an object that implements the  
	 * Connectable interface. The data encoding is determined by the
	 * Connectable interface.
	 * 
	 * @param con a reference to a Connectable object. This reference will
	 * be used to communicate with the server
	 */
	public Transfer(Connectable con) {
		this(con, con.getEncoding());
	}
	
	/**
	 * Returns the message from the buffer specified by 
	 * the supplied index.
	 * 
	 * @param idx the message index
	 * @return the message
	 */
	public T getMessage(int idx) {
		return messages.get(idx);
	}
	
	/**
	 * Returns the first message from the buffer.
	 * 
	 * @return the first message
	 */
	public T firstMessage() {
		return messages.firstElement();
	}
	
	/**
	 * Returns the last message from the buffer.
	 * 
	 * @return the last message
	 */
	public T lastMessage() {
		return messages.lastElement();
	}
	
	/**
	 * Returns all messages from the buffer.
	 * 
	 * @return a list off all messages
	 */
	public List<T> getMessages() {
		return messages;
	}
	
	/**
	 * Returns a String representation off all messages
	 * that are currently in the buffer. This representation can
	 * be used to directly communicate with the SLNP server.
	 * 
	 * @return a String representation of the message buffer
	 */
	public String getSLNPMessage() {
		
		// allocate buffer with initial capacity
		StringBuffer buffer = new StringBuffer(messages.size() * 16);
		
		for (Message m : messages) {
			buffer.append(m.getSLNPMessage());
		}
		return buffer.toString();
	}
	
	/**
	 * Adds a new message to the buffer.
	 * 
	 * @param msg the message to be added
	 */
	public void addMessage(T msg) {
		messages.addElement(msg);
	}
	
	/**
	 * Clears the internal message buffer.
	 */
	public void clear() {
		messages.clear();
	}
	
	/**
	 * Returns the encoding that is currently used for
	 * the SLNP communication.
	 * 
	 * @return the data encoding
	 */
	public Charset getEncoding() {
		return encoding;
	}
	
	/**
	 * Sets the encoding that should be used for the
	 * communication with the SLNP server.
	 * 
	 * @param encoding the encoding
	 */
	public void setEncoding(Charset encoding) {
		this.encoding = encoding;
		// update streams with new encoding
		setStreams();
	}
	
	/**
	 * Checks if the at least one message is currently available
	 * in the buffer and if all messages in the buffer are valid. 
	 * 
	 * @return true if the buffer contains data and all 
	 * messages in the buffer are valid
	 */
	public boolean isValid() {
		
		// check if buffer includes at least one message
		if (messages.size() < 1) return false;
		
		// check if all messages in the buffer are valid
		for (Message m : messages)
			if (!m.isValid()) return false;
		
		return true;
	}
	
	/**
	 * Returns the connection that is used for the communicatuion
	 * with the SLNP server.
	 * 
	 * @return the connection
	 */
	public Connectable getConnection() {
		return con;
	}
	
	/**
	 * Returns a BufferedReader for reading data from 
	 * the SLNP server.
	 * 
	 * @return a reader for reading data
	 */
	public BufferedReader getReader() {
		return in;
	}
	
	/**
	 * Returns a PrintWriter for writing data to 
	 * the SLNP server.
	 * 
	 * @return a writer for writing data
	 */
	public PrintWriter getWriter() {
		return out;
	}
	
	/**
	 * Returns the size of the message buffer.
	 * 
	 * @return the size of the buffer
	 */
	public int getSize() {
		return messages.size();
	}
	
	@Override
	public String toString() {
		return messages.toString();
	}
	
	/**
	 * Creates reader and writer streams for communicating
	 * with the SLNP server.
	 */
	private void setStreams() {
		// create input reader for current encoding
		in = new BufferedReader(new InputStreamReader(
				con.getInputStream(), encoding));
		// create output writer for current encoding
		out = new PrintWriter(new OutputStreamWriter(
				con.getOutputStream(), encoding));
	}
}
