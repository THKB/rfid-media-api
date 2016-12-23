package de.thkoeln.bibl.api.rfid.reader;

import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmException;
import de.feig.FedmIscReader;

/**
 * Class implements a basic reader connection. The class describes the
 * general methods a specializing class must implement to communicate
 * with a reader.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public abstract class ReaderConnection {
	
	protected FedmIscReader reader;
	
	/**
	 * Initialize a new ReaderConnection.
	 * 
	 * @throws ReaderException if the initialization of the reader connection
	 * failed 
	 */
	public ReaderConnection() throws ReaderException {
		
		try { reader = new FedmIscReader(); }
		catch (Exception e) { 
			throw new ReaderException("could not initialize reader connection", e);
		}
	}
	
	/**
	 * Checks if the reader connection is currently connected.
	 * 
	 * @return true if the connection is connected
	 */
	public abstract boolean isConnected();
	
	/**
	 * Opens the connection to the RFID device.
	 * 
	 * @throws FedmException if the Feig handler throws an exception 
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public abstract void open() throws FedmException, 
			FePortDriverException, FeReaderDriverException;
	
	/**
	 * Closes the connection to the RFID device.
	 * 
	 * @throws FedmException if the Feig handler throws an exception 
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public abstract void close() throws FedmException, 
			FePortDriverException, FeReaderDriverException;
}
