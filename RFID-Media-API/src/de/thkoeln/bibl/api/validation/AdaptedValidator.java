package de.thkoeln.bibl.api.validation;

/**
 * Interface adds support for validating an object pair against each other.
 * Implementing class must describe the validating logic for the object pair
 * specified by the interface parameter T1 and T2.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T1> first type of object to validate
 * @param <T2> second type of object to validate
 */
public interface AdaptedValidator <T1, T2> {
	
	/**
	 * Validates the object pair against each other 
	 * and creates a ValidateResult.
	 * 
	 * @param obj1 first object to validate
	 * @param obj2 second object to validate
	 * @return a result as ValidateResult
	 * @throws ValidationException if validation failed
	 */
	public ValidateResult<T1> validate(T1 obj1, T2 obj2) throws ValidationException;
}
