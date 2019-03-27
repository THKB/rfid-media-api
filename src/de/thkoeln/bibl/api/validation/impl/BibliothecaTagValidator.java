package de.thkoeln.bibl.api.validation.impl;

import java.util.List;

import de.thkoeln.bibl.api.rfid.tag.BibliothecaTag;
import de.thkoeln.bibl.api.rfid.tag.data.DDMDataBibliotheca;
import de.thkoeln.bibl.api.validation.ValidateResult;
import de.thkoeln.bibl.api.validation.Validator;

/**
 * Implements a validator for bibliotheca tags. It can be used to validate
 * a BibliothecaTag.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class BibliothecaTagValidator implements Validator<BibliothecaTag> {
	
	private String bibID;
	private String nation;
	private List<Integer> afiValues;
	
	/**
	 * Initialize a new BibliothecaTagValidator with the required 
	 * information.
	 * 
	 * @param bibID the bib ID to check the tag against
	 * @param nation the nation to check the tag against
	 * @param afiValues a list of valid AFI values the can have
	 */
	public BibliothecaTagValidator(String bibID, String nation, 
			List<Integer> afiValues) {
		
		this.bibID = bibID.trim();
		this.nation = nation.trim();
		this.afiValues = afiValues;
	}

	/**
	 * Validates the Bibliotheca tag. The validation includes CRC 
	 * verification, nation verification, bib ID verification,
	 * part number verification and AFI verification when the tag
	 * supports the AFI.
	 */
	@Override
	public ValidateResult<BibliothecaTag> validate(BibliothecaTag tag) {
		
		ValidateResult<BibliothecaTag> re = new ValidateResult<>(tag);
		DDMDataBibliotheca data = tag.getDataModel();
		
		// verify CRC
		if (!data.isValid())
			re.add("invalid checksum");
		
		// verify nation
		if (!data.getNation().equalsIgnoreCase(nation))
			re.add("invalid nation '%s'", data.getNation());
		
		// verify bib ID
		if (!data.getLibID().equalsIgnoreCase(bibID))
			re.add("invalid library id '%s'", data.getLibID());
		
		// verify part number
		if (data.getPartNr() > data.getParts())
			re.add("invalid part number '%d'", data.getPartNr());
		
		// skip AFI check if not supported
		if (!tag.supportsAFI()) return re;
		
		// verify AFI
		if (!afiValues.contains(tag.getAFI()))
			re.add("invalid security byte '%02X'", tag.getAFI());
		
		return re;
	}
}
