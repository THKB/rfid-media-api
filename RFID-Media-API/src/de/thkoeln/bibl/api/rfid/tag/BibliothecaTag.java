package de.thkoeln.bibl.api.rfid.tag;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmBrmTableItem;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIsoTableItem;
import de.feig.TagHandler.FedmIscTagHandler_ISO15693;
import de.thkoeln.bibl.api.rfid.tag.data.DDMDataBibliotheca;

/**
 * Class extends the ISO 15693 RFID tag with the Bibliotheca specific
 * data model, which is based in the default DDM (Daenisches Daten Model)
 * model. Communication is based on the Feig IscTagHandler API.
 * 
 * Instances of this class support XML serializing by JAXB.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
@XmlRootElement(name="bibliotheca-tag")
@XmlAccessorType(XmlAccessType.NONE)
public class BibliothecaTag extends ISO15693Tag {

	private static final long serialVersionUID = 2845935520930829738L;
	
	@XmlElement (name = "data-model", required = true)
	private DDMDataBibliotheca model;
	
	/**
	 * Default constructor for serialization.
	 */
	protected BibliothecaTag() {}
	
	/**
	 * Initialize a new BibliothecaTag with the supplied tag handler.
	 * 
	 * @param handler the handler of the tag
	 * @throws TagIOException if the communication with the tag failed
	 * @throws FedmException if the Feig handler throws an exception
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public BibliothecaTag(FedmIscTagHandler_ISO15693 handler) throws TagIOException, 
			FedmException, FePortDriverException, FeReaderDriverException  {
		
		super(handler);
		
		model = new DDMDataBibliotheca(getData());
	}
	
	/**
	 * Initialize a new BibliothecaTag with the supplied tag reader and
	 * BRM table tag.
	 * 
	 * @param reader the reader used for communication with the tag
	 * @param tag the tag as BRM table item
	 * @throws TagIOException if the communication with the tag failed
	 * @throws FedmException if the Feig handler throws an exception 
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public BibliothecaTag(FedmIscReader reader, FedmBrmTableItem tag) throws TagIOException, 
			FedmException, FePortDriverException, FeReaderDriverException {
		
		super(reader, tag);
		
		model = new DDMDataBibliotheca(getData());
	}
	
	/**
	 * Initialize a new BibliothecaTag with the supplied tag reader and
	 * ISO table tag.
	 * 
	 * @param reader the reader used for communication with the tag
	 * @param tag the tag as ISO table item
	 * @throws TagIOException if the communication with the tag failed
	 * @throws FedmException if the Feig handler throws an exception 
	 * @throws FePortDriverException if the communication with the RFID
	 * device port failed
	 * @throws FeReaderDriverException if the communication with the RFID
	 * device driver failed
	 */
	public BibliothecaTag(FedmIscReader reader, FedmIsoTableItem tag) throws TagIOException, 
			FedmException, FePortDriverException, FeReaderDriverException {
		
		super(reader, tag);
		
		model = new DDMDataBibliotheca(getData());
	}
	
	/**
	 * Returns the data model of the tag.
	 * 
	 * @return the data model
	 */
	public DDMDataBibliotheca getDataModel() {
		return model;
	}
	
	@Override
	public String toString() {
		return String.format("%s (%s)", model, getUID());
	}
	
	/**
	 * Compares the tag by it's data model.
	 * 
	 * @param obj the tag to compare with
	 * @return a negative integer, zero, or a positive integer as this 
	 * object is less than, equal to, or greater than the specified object.
	 */
	public int compareTo(BibliothecaTag obj) {
		return this.getDataModel().compareTo(obj.getDataModel());
	}
}
