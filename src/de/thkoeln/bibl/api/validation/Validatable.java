package de.thkoeln.bibl.api.validation;

/**
 * An object which implements the interface can be verified.
 * Implementing class must describe the validating logic. To validate 
 * the object there is a set of default validator classes in 
 * @see de.thkoeln.bibl.api.validation.impl
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 * 
 * @param <T> type of validatable object
 */
public interface Validatable<T> {

	/**
	 * Validates the object and returns a ValidateResult.
	 * 
	 * @return a validation result as ValidateResult
	 * @throws ValidationException if the validation failed
	 */
	public ValidateResult<T> validate() throws ValidationException;
}
