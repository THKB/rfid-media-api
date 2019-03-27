package de.thkoeln.bibl.api.lms;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface describes default methods to communicate with an LMS.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public interface Connectable {
	
	/**
	 * Opens a connection to the LMS
	 * 
	 * @throws LMSException if an error occurred while opening the connection
	 */
	public void open() throws LMSException;
	
	/**
	 * Closes the connection to the LMS
	 * 
	 * @throws LMSException if an error occurred while closing the connection
	 */
	public void close() throws LMSException;
	
	/**
	 * Returns an input stream for reading from the LMS connection
	 * 
	 * @return an readable input stream
	 */
	public InputStream getInputStream();
	
	/**
	 * Returns an output stream for writing to the LMS connection
	 * 
	 * @return an writable output stream
	 */
	public OutputStream getOutputStream();
	
	/**
	 * Returns the character encoding of the connection
	 * 
	 * @return a string representation of the charset encoding
	 */
	public String getEncoding();
	
	/**
	 * Returns the connection status
	 * 
	 * @return a boolean indicating the connection status
	 */
	public boolean isConnected();
}
