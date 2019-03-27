package de.thkoeln.bibl.api.validation;

/**
 * Interface adds support for validating an object.
 * Implementing class must describe the validating logic for the object type
 * specified by the interface parameter T.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> type of object to validate
 */
public interface Validator<T> {

	/**
	 * Validates the object and returns a ValidateResult.
	 * 
	 * @param obj object to validate
	 * @return a result as ValidateResult
	 * @throws ValidationException if the validation failed
	 */
	public ValidateResult<T> validate(T obj) throws ValidationException;
}
