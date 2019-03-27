package de.thkoeln.bibl.api.lms.slnp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.thkoeln.bibl.api.lms.LMSException;
import de.thkoeln.bibl.api.lms.Searchable;
import de.thkoeln.bibl.api.lms.slnp.MediaSearchRequest.SearchType;
import de.thkoeln.bibl.api.media.AbstractMediaNumber;
import de.thkoeln.bibl.api.media.MediaException;
import de.thkoeln.bibl.api.media.SisisMedia;

/**
 * Class implements a SLNP client to communicate with a LMS.
 * The class supports multiple SLNP connections to optimize the response
 * time for media lookups.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <N> the media number type the client creates
 */
public class SLNPClient<N extends AbstractMediaNumber> 
	implements Searchable<SisisMedia<N>> {
	
	/**
	 * The connection status describes the actual state of a connection.
	 */
	private static enum ConStatus {
		READY, BUSY;
				
		public boolean isReady() {
			return (this == READY);
		}
	}
	
	private int searchLimit;
	private int poolLimit;
	private SLNPConnection refCon;
	private Map<SLNPConnection, ConStatus> conPool;
	private final Constructor<N> mediaNrBuilder;
	
	/**
	 * Initialize a SLNPClient with the supplied SLNPConnection object.
	 * 
	 * @param mediaNrClass the media number class for which the client builds
	 * the media
	 * @param con the SLNP connection used for communication
	 * @throws ReflectiveOperationException if the supplied media number class dosn't
	 * support the required initialization function
	 */
	public SLNPClient(Class<N> mediaNrClass, SLNPConnection con) 
			throws ReflectiveOperationException {
	
		// default values
		searchLimit = 500;
		poolLimit = 10;
		
		// get media number constructor
		mediaNrBuilder = mediaNrClass.getDeclaredConstructor(String.class);
		
		// initialize connection pool
		conPool = new HashMap<>(poolLimit);
		refCon = con;
		conPool.put(refCon, ConStatus.READY);
	}
	
	/**
	 * Initialize a SLNPClient with the supplied connection information
	 * and login credentials.
	 * 
	 * @param mediaNrClass the media number class for which the client builds
	 * the media
	 * @param host the host address to connect to
	 * @param port the port to connect to
	 * @param cred the credentials to use for authentication
	 * @throws ReflectiveOperationException if the supplied media number class dosn't
	 * support the required initialization function
	 */
	public SLNPClient(Class<N> mediaNrClass, String host, int port, 
			Credentials cred) throws ReflectiveOperationException {
		
		this(mediaNrClass, new SLNPConnection(host, port, cred));
	}
	
	/**
	 * Initialize a SLNPClient with the supplied connection and login
	 * information.
	 * 
	 * @param mediaNrClass the media number class for which the client builds
	 * the media
	 * @param host the host address to connect to
	 * @param port the port to connect to
	 * @param user the username to use for the login process
	 * @param pass the password to use for the login process
	 * @param branchId the branch ID to use for the login process
	 * @throws ReflectiveOperationException if the supplied media number class dosn't
	 * support the required initialization function
	 */
	public SLNPClient(Class<N> mediaNrClass, String host, int port, 
			String user, String pass, int branchId) throws ReflectiveOperationException {
		
		this(mediaNrClass, host, port, new Credentials(user, pass, branchId));
	}
	
	/**
	 * Returns the search limit.
	 * 
	 * @return the search limit
	 */
	public int getSearchLimit() {
		return searchLimit;
	}
	
	/**
	 * Sets the limit for each media search. A single search request
	 * will never request more results than this limit.
	 * 
	 * @param limit maximum results per search
	 */
	public void setSearchLimit(int limit) {
		searchLimit = limit;
	}
	
	/**
	 * Returns the connection limit.
	 * 
	 * @return the connection limit
	 */
	public int getConnectionLimit() {
		return poolLimit;
	}
	
	/**
	 * Sets the maximum connections a client can open in parallel.
	 * 
	 * @param limit maximum connections
	 */
	public void setConnectionLimit(int limit) {
		poolLimit = limit;
	}
	
	/**
	 * Sets the SLNPConnection timeout.
	 * 
	 * @param timeout the timeout in milliseconds
	 * @throws SocketException if there is an error in the 
	 * underlying protocol, such as a TCP error
	 */
	public void setTimeout(int timeout) throws SocketException {
		for (SLNPConnection c : getConnections())
			c.setSoTimeout(timeout);
	}
	
	/**
	 * Returns a list of SLNPConnection objects used for communicating
	 * with the SLNP server.
	 * 
	 * @return a list of SLNPConnection objects
	 */
	public Set<SLNPConnection> getConnections() {
		return conPool.keySet();
	}
	
	/**
	 * Checks if at least one connection to the LMS is connected.
	 * 
	 * @return true if a connection is connected
	 */
	public boolean isConnected() {
		// verify at least one connection is connected
		for (SLNPConnection c : getConnections())
			if (c.isConnected()) return true;
		// no connection is connected
		return false;
	}
	
	/**
	 * Opens all underlying SLNP connections.
	 * 
	 * @throws LMSException if an error occurred while opening the connection
	 */
	public void open() throws LMSException {
		for (SLNPConnection c : getConnections()) c.open();
	}
	
	/**
	 * Closes all underlying SLNP connections.
	 * 
	 * @throws LMSException if an error occurred while closing the connection
	 */
	public void close() throws LMSException {
		for (SLNPConnection c : getConnections()) c.close();
	}

	@Override
	public SisisMedia<N> lookupMedia(String id) throws LMSException {
		// lookup media by unique ID
		return requestMedia(id.trim(), 1, 1, SearchType.MEDIA_NUMBER).get(id.trim());
	}
	
	@Override
	public Map<String, SisisMedia<N>> searchMedia(String signature) throws LMSException {
		
		int hitStart = 1;
		Map<String, SisisMedia<N>> media = new HashMap<>();
		
		// request media in small chunks
		while(true) {
			// lookup media by signature
			Map<String, SisisMedia<N>> re = 
					requestMedia(signature.trim(), searchLimit, hitStart, SearchType.SIGNATURE);
			
			// stop if last request was empty
			if (re.isEmpty()) break;
			
			// add media to result map
			media.putAll(re);
			
			// stop if last request was less than search limit
			if (re.size() < searchLimit) break;
			
			// move start index to next chunk
			hitStart += re.size();
		}
		
		return media;
	}
	
	/**
	 * Creates a new SisisMedia object from a map of key value data pairs.
	 * 
	 * @param id the media ID / signature
	 * @param data a map of key value pairs
	 * @return the created media object
	 * @throws MediaException if the media creation failed
	 */
	private SisisMedia<N> createMedia(String id, Map<String,String> data) 
			throws MediaException {
		
		N mnr = null;
		// create media number instance
		try { mnr = mediaNrBuilder.newInstance(id); }
		catch (InvocationTargetException e) {
			throw new MediaException("could not create media number object", 
					e.getTargetException());
		}
		catch (ReflectiveOperationException e) {
			throw new MediaException("could not create media number object", e);
		}
		
		SisisMedia<N> media = new SisisMedia<>(mnr);
		
		// non critical values
		media.setTitle(data.get("Titel"));
		media.setAuthor(data.get("Verfasser"));
		media.setBorrowState(Integer.parseInt(data.get("Status")));
		media.setSignature(data.get("Signatur"));
		media.setLoanAbility(data.get("Entl"));
		
		// critical values
		try { media.setYear(Integer.parseInt(data.get("Jahr"))); } catch (Exception e) {}
		try { media.setLocation(Integer.parseInt(data.get("HeimatZweigstelle"))); } catch (Exception e) {}
		try { media.setParts(Integer.parseInt(data.get("Beilage"))+1); } catch (Exception e) {}
		try { media.setType(Integer.parseInt(data.get("MedienTyp"))); } catch (Exception e) {}
		try { media.setCreated(data.get("AufnahmeDatum")); } catch (Exception e) {}
		try { media.setDamaged(data.get("Beschaedigt")); } catch (Exception e) {}
		try { media.setAttachment(data.get("IstBeilage")); } catch (Exception e) {}
		try { media.setAttachment(data.get("IstBeilage")); } catch (Exception e) {}

		return media;
	}

	/**
	 * Request media information specified by search string. Returns a map of found
	 * media object which where found by the the search string, indexed by the media-id.
	 * 
	 * @param searchStr the search string to lookup media for
	 * @param hitMax the maximum hits the result should have
	 * @param hitStart start index in the hits
	 * @param type specifies the type of search
	 * @return vector with media objects
	 * @throws LMSException if a LMS related error occurred
	 * @throws SLNPException if a SLNP related error occurred
	 */
	private Map<String, SisisMedia<N>> requestMedia(String searchStr, int hitMax, 
			int hitStart, SearchType type) throws LMSException, SLNPException {
		
		// get a ready SLNP connection
		SLNPConnection con = getConnection();
		if (con == null) throw new SLNPException("all connections busy");
		
		MediaSearchResponse res = null;
		
		try {
			MediaSearchRequest req;
			
			// verify a valid hit count
			if (hitMax < 1) hitMax = 1;
			
			// create media search request
			req = new MediaSearchRequest(
					con, con.getCredentials(), searchStr, type, hitMax, hitStart);
			
			// send request
			req.send();
		
			// receive and parse the response
			res = new MediaSearchResponse(con);
			res.receive();
			res.parse();
		}
		catch (Exception e) {
			throw new LMSException("could not lookup media", e);
		}
		finally {
			releaseConnection(con);
		}
		
		// check for response errors
		if (!res.isSuccessful()) throw new SLNPException(
				"slnp protocol error: " + res.getError());
		
		// create new vector for media objects
		HashMap<String, SisisMedia<N>> media = new HashMap<>(res.getDataSet().size());
		
		// build for every data set a new media object
		for (Entry<String, Map<String, String>> elm : res.getEntrySet()) {
			
			String id = elm.getKey();
			// create media object and add to map
			try { media.put(id, createMedia(id, elm.getValue())); }
			catch(MediaException e) {
				throw new LMSException("could not create media object", e);
			}
		}
		
		return media;
	}
	
	/**
	 * Returns a ready connection from the pool. The returned connection
	 * is exclusively reserved until its released with releaseConnection().
	 * 
	 * @return ready connection or null if no connection ready 
	 * and maximum pool size reached.
	 * @throws LMSException if a connection could not opened
	 */
	private synchronized SLNPConnection getConnection() throws LMSException {
		
		// search for ready connection
		for (Entry<SLNPConnection, ConStatus> elm : conPool.entrySet())
			if (elm.getValue().isReady()) {
				// verify connection is connected
				elm.getKey().open();
				// mark connection as busy
				elm.setValue(ConStatus.BUSY);
				// return connection
				return elm.getKey();
			}
		
		// check maximum pool size not reached
		if (conPool.size() >= poolLimit) return null;
		
		// create new connection if all connections busy
		SLNPConnection con = new SLNPConnection(refCon);
		// open connection
		con.open();
		// add connection to pool
		conPool.put(con, ConStatus.BUSY);
		
		return con;
	}
	
	/**
	 * Releases a connection for new task.
	 * 
	 * @param con the connection to be released
	 */
	private synchronized void releaseConnection(SLNPConnection con) {
		conPool.put(con, ConStatus.READY);
	}
}
