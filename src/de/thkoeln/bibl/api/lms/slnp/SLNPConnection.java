package de.thkoeln.bibl.api.lms.slnp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

import de.thkoeln.bibl.api.lms.Connectable;
import de.thkoeln.bibl.api.lms.LMSException;
import de.thkoeln.bibl.api.lms.slnp.message.MessageFormatException;

/**
 * Class implements a single SLNP connection. This connection controls the  
 * communication between client and SLNP server.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 */
public class SLNPConnection implements Connectable {
	
	private InetSocketAddress address;
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private String encoding;
	private Credentials cred;
	private int socketTimeout;
	
	/**
	 * Initialize a SLNPConnection with the supplied network address
	 * and uses the credential to authenticate at the server.
	 * 
	 * @param address the network address to connect to
	 * @param cred the credentials to use for authentication
	 */
	public SLNPConnection(InetSocketAddress address, Credentials cred) {
		
		this.cred = cred;
		this.address = address;
		
		// default values
		socketTimeout = 2000;
		encoding = "UTF-8";
	}
	
	/**
	 * Initialize a SLNPConnection with the supplied host address/port
	 * and uses the credential to authenticate at the server.
	 * 
	 * @param host the host address to connect to
	 * @param port the port to connect to
	 * @param cred the credentials to use for authentication
	 */
	public SLNPConnection(String host, int port, Credentials cred) {
		
		this(new InetSocketAddress(host, port), cred);
	}
	
	/**
	 * Initialize a SLNPConnection with the supplied host address/port
	 * and uses the login information to authenticate at the server.
	 * 
	 * @param host the host address to connect to 
	 * @param port the port to connect to
	 * @param user the username to use for the login process
	 * @param pass the password to use for the login process
	 * @param branchId the branch ID to use for the login process
	 */
	public SLNPConnection(String host, int port, String user, 
			String pass, int branchId) {
		
		this(host, port, new Credentials(user, pass, branchId));
	}
	
	/**
	 * Initialize a SLNPConnection with the supplied SLNPConnection object.
	 * The login credentials and server address is used from the supplied
	 * SLNPConnection object.
	 * 
	 * @param con the SLNPConnection to use the connection information from
	 */
	public SLNPConnection(SLNPConnection con) {
		this(con.getAddress(), con.getCredentials());
	}
	
	/**
	 * Set the maximum time a socket will wait for connection or I/O
	 * operations.
	 * 
	 * @param timeout maximum time to wait in milliseconds
	 * @throws SocketException if there is an error in the underlying protocol, 
	 * such as a TCP error.
	 */
	public void setSoTimeout(int timeout) throws SocketException {
		socketTimeout = timeout;
		if (socket != null) socket.setSoTimeout(timeout);
	}
	
	/**
	 * Returns the credentials used to authenticate at the server.
	 * 
	 * @return the credentials
	 */
	public Credentials getCredentials() {
		return cred;
	}
	
	/**
	 * Returns the network address used to connect to the server.
	 * 
	 * @return the network address
	 */
	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public void open() throws LMSException {
		
		// skip if already connected
		if (isConnected()) return; 
		
		try {
			socket = new Socket();
			
			// set reading timeout
			socket.setSoTimeout(socketTimeout);
		
			// connect to SLNP server
			socket.connect(address);
		
			// register I/O objects
			out = socket.getOutputStream();
			in = socket.getInputStream();
		
			// process server connect
			connect();
		
			// process login
			login();
		}
		catch (Exception e) {
			throw new LMSException("could not open connection", e);
		}
	}

	@Override
	public void close() throws LMSException {
		
		// skip if not connected
		if (!isConnected()) return;
		
		try {
			out.close();
			in.close();
			socket.close();
		}
		catch (IOException e) {
			throw new LMSException("could not close connection", e);
		}
	}
	
	@Override
	public InputStream getInputStream() {
		return in;
	}
	

	@Override
	public OutputStream getOutputStream() {
		return out;
	}
	

	@Override
	public String getEncoding() {
		return encoding;
	}
	
	@Override
	public boolean isConnected() {
		return socket != null && !socket.isClosed() 
				&& socket.isConnected();
	}
	
	/**
	 * Initiates a connection request and validates the response.
	 * 
	 * @throws IOException if communication with the server failed
	 * @throws MessageFormatException if the internally message creation
	 * fails
	 * @throws ReflectiveOperationException if the response message dosn't 
	 * support the required initialization functionality
	 * @throws SLNPException if the connect response was not successful
	 */
	private void connect() throws IOException, MessageFormatException, 
			ReflectiveOperationException, SLNPException {
		
		ConnectResponse res = new ConnectResponse(this);
		res.receive();
		
		if (!res.isSuccessful()) throw new SLNPException("connect failed");
		
		encoding = res.getMessage().getCharset();
	}
	
	/**
	 * Initiates a login request and validates the response.
	 * 
	 * @throws IOException if communication with the server failed
	 * @throws MessageFormatException if the internally message creation
	 * fails
	 * @throws ReflectiveOperationException if the response message dosn't 
	 * support the required initialization functionality
	 * @throws SLNPException if the login response was not successful
	 */
	private void login() throws IOException, MessageFormatException, 
			ReflectiveOperationException, SLNPException  {
		
		LoginRequest req = new LoginRequest(this, cred);
		req.send();
		
		LoginResponse res = new LoginResponse(this);
		res.receive();
		
		if (!res.isSuccessful()) throw new SLNPException("login failed");
	}
}