package de.thkoeln.bibl.api.validation.impl;

import java.util.Map;

import de.thkoeln.bibl.api.media.AbstractMediaNumber;
import de.thkoeln.bibl.api.media.SisisMedia;
import de.thkoeln.bibl.api.media.SisisMedia.BorrowState;
import de.thkoeln.bibl.api.media.SisisMedia.LoanAbility;
import de.thkoeln.bibl.api.rfid.tag.BibliothecaTag;
import de.thkoeln.bibl.api.rfid.tag.data.DDMDataBibliotheca;
import de.thkoeln.bibl.api.validation.AdaptedValidator;
import de.thkoeln.bibl.api.validation.ValidateResult;

/**
 * Implements a validator for bibliotheca tags which checks against a SISIS
 * media object.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> the type of media number used by the SISIS media.
 */
public class BibliothecaMediaValidator<T extends AbstractMediaNumber>
		implements AdaptedValidator<BibliothecaTag, SisisMedia<T>> {

	private Map<Integer, BorrowState> afiMap;
	private Map<Integer, LoanAbility> loanMap;

	/**
	 * Initialize a new BibliothecaMediaValidator with required the information.
	 * 
	 * @param afiMap  a map with AFI tag values and the associated borrow state
	 * @param loanMap a map with status tag values and the associated loanablility
	 */

	public BibliothecaMediaValidator(Map<Integer, BorrowState> afiMap, Map<Integer, LoanAbility> loanMap) {
		this.afiMap = afiMap;
		this.loanMap = loanMap;
	}

	/**
	 * Validates the Bibliotheca tag against the SISIS media and vice versa. The
	 * validation includes signature validation, media type validation, media parts
	 * validation, loanability status validation and borrow state validation if the
	 * tag supports the AFI.
	 */
	@Override
	public ValidateResult<BibliothecaTag> validate(BibliothecaTag tag, SisisMedia<T> media) {

		ValidateResult<BibliothecaTag> re = new ValidateResult<>(tag);
		DDMDataBibliotheca data = tag.getDataModel();

		// verify media signature
		if (!data.getSignature().equals(media.getMediaNumber().getNumber()))
			re.add("media signature differs '%s' <> '%s'", media.getMediaNumber(), data.getSignature());

		// verify media type
		if (data.getType() != media.getType())
			re.add("media type differ '%d' <> '%d'", media.getType(), data.getType());

		// verify media parts
		if (data.getParts() != media.getParts())
			re.add("media parts differ '%d' <> '%d'", media.getParts(), data.getParts());

		// verify media loanability
		if (loanMap.get(data.getStatus()).isLoanable() != media.getLoanAbility().isLoanable()
				&& !(data.getStatus() == 2 && media.getLoanAbility().isLoanable()))
			re.add("media loanability differ '%s' <> '%s'", media.getLoanAbility(), loanMap.get(data.getStatus()));

		// skip AFI check if not supported
		if (!tag.supportsAFI())
			return re;

		// verify media borrow state
		if (getOrDefault(afiMap, tag.getAFI(), BorrowState.UNKNOWN).isBorrowed() != media.getBorrowState().isBorrowed())
			re.add("media borrow state differ '%s' <> '%s'", media.getBorrowState(),
					getOrDefault(afiMap, tag.getAFI(), BorrowState.UNKNOWN));

		// verify media borrow state if both are out of house
		if (getOrDefault(afiMap, tag.getAFI(), BorrowState.UNKNOWN).isBorrowed() && media.getBorrowState().isBorrowed())
			re.add("media borrow state both out of house '%s' <> '%s'", media.getBorrowState(),
					getOrDefault(afiMap, tag.getAFI(), BorrowState.UNKNOWN));

		return re;
	}

	/**
	 * Returns the value from the supplied map to which the specified key is mapped,
	 * or a default value if the map contains no mapping for the key.
	 * 
	 * @param map      the map to get get the value from
	 * @param key      the key to which the value is mapped
	 * @param defValue the default value to return if no key was founds
	 * @return the mapped value or the default value
	 */
	private static <K, V> V getOrDefault(Map<K, V> map, K key, V defValue) {
		if (map.containsKey(key))
			return map.get(key);
		return defValue;
	}
}
