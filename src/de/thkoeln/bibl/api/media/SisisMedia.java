package de.thkoeln.bibl.api.media;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class extends the structure of a library media by the SISIS-SunRise 
 * specific data.
 * This class supports the serializing to/from XML data.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> the type of media number the sisis media used
 */
@XmlRootElement(name="sisis-media")
@XmlAccessorType(XmlAccessType.NONE)
public class SisisMedia<T extends AbstractMediaNumber> extends LibraryMedia<T> {

	private static final long serialVersionUID = -39689167883749986L;
	
	/**
	 * The BorrowState describes the borrow status of the SISIS media.
	 */
	public static enum BorrowState {
		
		NOT_BORROWED(0, false),
		
		BORROWED(4, true),
		
		RESERVED(88, false),
		
		UNKNOWN(-1, false);
		
		private int code;
		private boolean borrowed;
		
		private BorrowState(int code, boolean borrowed) {
			this.code = code;
			this.borrowed = borrowed;
		}
		
		/**
		 * Checks it the BorrowState is borrowed.
		 * 
		 * @return true if the BorrowState indicates a borrowed media
		 */
		public boolean isBorrowed() {
			return borrowed;
		}
		
		/**
		 * Returns the BorrowState for a supplied SISIS specific
		 * borrow code.
		 * 
		 * @param code the SISIS specific code
		 * @return the corresponding BorrowState
		 */
		private static BorrowState getEnum(int code) {
			for(BorrowState e : BorrowState.values()) {
				if (e.code == code) return e;
			}
			return UNKNOWN;
		}
	}
	
	/**
	 * The LoanAbility describes the loanable status of the SISIS media.
	 */
	public static enum LoanAbility {
		
		LOANABLE("", true),
		
		LOANABLE_WEEKEND("W", true),
		
		LOANABLE_ROOM("L", true),
		
		LOANABLE_SPECIAL_ROOM("B", true),
		
		NOT_LOANABLE("X", false),
		
		UNKNOWN("UNKNOWN", false);
		
		private String code;
		private boolean loanable;
		
		private LoanAbility(String code, boolean loanable) {
			this.code = code;
			this.loanable = loanable;
		}
		
		/**
		 * Checks it the LoanAbility is loanable.
		 * 
		 * @return true if the LoanAbility indicates a loanable media
		 */
		public boolean isLoanable() {
			return loanable;
		}
		
		/**
		 * Returns the LoanAbility for a supplied SISIS specific
		 * loanable code.
		 * 
		 * @param code the SISIS specific code
		 * @return the corresponding LoanAbility
		 */
		private static LoanAbility getEnum(String code) {
			for(LoanAbility e : LoanAbility.values()) {
				if (e.code.equalsIgnoreCase(code.trim())) return e;
			}
			return UNKNOWN;
		}
	}
	
	@XmlElement (name = "signature", required = true)
	private String signature;
	
	@XmlElement (name = "location", required = true)
	private int location;
	
	@XmlElement (name = "type", required = true)
	private int type;
	
	@XmlElement (name = "attachment", required = true)
	private boolean isAttachment;
	
	@XmlElement (name = "parts", required = true)
	private int parts;
	
	@XmlElement (name = "damaged", required = true)
	private boolean damaged;
	
	@XmlElement (name = "created", required = true)
	private Date created;
	
	@XmlElement (name = "borrowState", required = true)
	private BorrowState borrowState;
	
	@XmlElement (name = "loanAbility", required = true)
	private LoanAbility loanAbility;
	
	/**
	 * Default constructor for serialization.
	 */
	protected SisisMedia() {}
		
	/**
	 * Initialize a new SisisMedia with the supplied media number.
	 * 
	 * @param mediaNumber the media number of the media
	 */
	public SisisMedia(T mediaNumber) {
		super(mediaNumber);
	}
	
	/**
	 * Sets the media signature.
	 * 
	 * @param signature the signature to be set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	/**
	 * Returns the media signature ot the media.
	 * 
	 * @return the media signature
	 */
	public String getSignature() {
		return signature;
	}
	
	/**
	 * Sets the location of the media.
	 * 
	 * @param location the location to be set
	 */
	public void setLocation(int location) {
		this.location = location;
	}
	
	/**
	 * Returns the location of the media.
	 * 
	 * @return the location
	 */
	public int getLocation() {
		return location;
	}
	
	/**
	 * Sets the type of the media.
	 * 
	 * @param type the type to be set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * Returns the type of the media.
	 * 
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Sets the attachment flag of the media.
	 * 
	 * @param isAttachment the attachment flag to be set
	 */
	public void setAttachment(boolean isAttachment) {
		this.isAttachment = isAttachment;
	}
	
	/**
	 * Sets the attachment flag of the media with the SISIS
	 * specific code.
	 * 
	 * @param isAttachment the attachment code to be set
	 */
	public void setAttachment(String isAttachment) {
		setAttachment(isAttachment.equalsIgnoreCase("J") ? true : false);
	}
	
	/**
	 * Checks if the media is an attachment.
	 * 
	 * @return true if the media is an attachment
	 */
	public boolean isAttachment() {
		return isAttachment;
	}
	
	/**
	 * Sets the number of media parts.
	 * 
	 * @param parts the number of parts
	 */
	public void setParts(int parts) {
		this.parts = parts;
	}
	
	/**
	 * Returns the number of media parts.
	 * 
	 * @return the number of parts
	 */
	public int getParts() {
		return parts;
	}
	
	/**
	 * Checks if the media has parts.
	 * 
	 * @return true if the media has parts
	 */
	public boolean hasParts() {
		return (parts > 1) ? true : false;
	}
	
	/**
	 * Sets the damaged flag of the media.
	 * 
	 * @param damaged the damaged flag to be set
	 */
	public void setDamaged(boolean damaged) {
		this.damaged = damaged;
	}
	
	/**
	 * Sets the damaged flag of the media with the SISIS
	 * specific code.
	 * 
	 * @param damaged the damaged code to be set
	 */
	public void setDamaged(String damaged) {
		setDamaged(damaged.equalsIgnoreCase("X") ? true : false);
	}
	
	/**
	 * Checks if the media is damaged.
	 * 
	 * @return true if the media is damaged
	 */
	public boolean isDamaged() {
		return damaged;
	}
	
	/**
	 * Sets the creation time of the media.
	 * 
	 * @param created the creation time to be set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/**
	 * Sets the creation time of the media with a String timestamp.
	 * The time stamp mast have the following format: dd.MM.yyyy
	 * 
	 * @param date the creation timestamp to be set
	 * @throws ParseException if the timestamp has an invalid format
	 */
	public void setCreated(String date) throws ParseException {
		
		DateFormat f = new SimpleDateFormat("dd.MM.yyyy");
		f.setLenient(false);
		setCreated(f.parse(date));
	}
	
	/**
	 * Returns the creation time of the media.
	 * 
	 * @return the creation time
	 */
	public Date getCreated() {
		return created;
	}
	
	/**
	 * Sets the borrow state of the media.
	 * 
	 * @param state the borrow state to be set
	 */
	public void setBorrowState(BorrowState state) {
		this.borrowState = state;
	}
	
	/**
	 * Sets the borrow state of the media with the SISIS 
	 * specific borrow code.
	 * 
	 * @param state the state to be set
	 */
	public void setBorrowState(int state) {
		setBorrowState(BorrowState.getEnum(state));
	}
	
	/**
	 * Returns the borrow state of the media.
	 * 
	 * @return the borrow state
	 */
	public BorrowState getBorrowState() {
		return borrowState;
	}
	
	/**
	 * Sets the loanability of the media.
	 * 
	 * @param value the loanability to be set
	 */
	public void setLoanAbility(LoanAbility value) {
		this.loanAbility = value;
	}
	
	/**
	 * Sets the loanability of the media with the SISIS 
	 * specific loanability code.
	 * 
	 * @param value the loanability to be set
	 */
	public void setLoanAbility(String value) {
		setLoanAbility(LoanAbility.getEnum(value));
	}
	
	/**
	 * Returns the loanability of the media.
	 * 
	 * @return the loanability
	 */
	public LoanAbility getLoanAbility() {
		return loanAbility;
	}
	
	/**
	 * Checks if the media is borrowed.
	 * 
	 * @return true if the media is borrowed
	 */
	public boolean isBorrowed() {
		return (borrowState == BorrowState.BORROWED);
	}
	
	/**
	 * Checks if the media is reserved.
	 * 
	 * @return true if the media is reserved
	 */
	public boolean isReserved() {
		return (borrowState == BorrowState.RESERVED);
	}
}
