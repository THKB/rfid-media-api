package de.thkoeln.bibl.api.rfid.reader;

import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmException;

/**
 * Class implements an IP based reader connection.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * BUG Feig reader.isConnected() implementation return true even when the
 * connection was terminated
 * TODO workaround for reader.isConnected() bug
 */
public class ReaderIPConnection extends ReaderConnection {
	
	private String host;
	private int port;
	
	/**
	 * Initialize a new ReaderConnection.
	 * 
	 * @param host the IP address of the reader 
	 * @param port the port to connect to
	 * @throws ReaderException if the initialization of the reader connection
	 * failed
	 */
	public ReaderIPConnection(String host, int port) throws ReaderException {
		this.host = host;
		this.port = port;
	}

	@Override
	public void open() throws FedmException, FePortDriverException, FeReaderDriverException {
		reader.connectTCP(host, port);
	}

	@Override
	public void close() throws FedmException, FePortDriverException, FeReaderDriverException {
		reader.disConnect();
	}

	@Override
	public boolean isConnected() {
		return reader.isConnected();
	}
}
