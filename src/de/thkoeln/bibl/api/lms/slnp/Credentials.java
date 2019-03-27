package de.thkoeln.bibl.api.lms.slnp;

/**
 * Class implements the credentials needed to communicate with a slnp server.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class Credentials {
	
	private String user;
	private String pass;
	private int branchId;
	
	/**
	 * Initialize a new credential with username, password and branch ID.
	 * 
	 * @param user the user name
	 * @param pass the password
	 * @param branchId the numeric sbranch identifier
	 */
	public Credentials(String user, String pass, int branchId) {
		this.user = user;
		this.pass = pass;
		this.branchId = branchId;
	}
	
	/**
	 * Returns the username.
	 * 
	 * @return the username
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Returns the password.
	 * 
	 * @return the password
	 */
	public String getPass() {
		return pass;
	}
	
	/**
	 * Returns the numeric branch ID.
	 * 
	 * @return the branch ID.
	 */
	public int getBranchID() {
		return branchId;
	}
}
