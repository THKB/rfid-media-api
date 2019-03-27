package de.thkoeln.bibl.api.validation;

import java.util.List;
import java.util.Vector;

/**
 * Class holds the all validation errors for a specific validated object.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 * @param <T> the type of the validation target
 */
public class ValidateResult<T> {
	
	private List<ValidateError> errors;
	private T target;
	
	/**
	 * Initialize a new ValidateResult for the specified object to validate.
	 * 
	 * @param target the object to validate
	 */
	public ValidateResult(T target) {
		errors = new Vector<>();
		this.target = target;
	}
	
	/**
	 * Adds a validation error to this result.
	 * 
	 * @param error the error to add
	 */
	public void add(ValidateError error) {
		errors.add(error);
	}
	
	/**
	 * Adds a new error message with values to this result.
	 * The method transparently creates a new ValidateError from
	 * the supplied information. The message supports the default
	 * format string syntax.
	 * 
	 * @param message the error message
	 * @param values replacement values for the message
	 */
	public void add(String message, Object... values) {
		add(new ValidateError(message, values));
	}
	
	/**
	 * Return a list with all errors.
	 * 
	 * @return a list with all errors
	 */
	public List<ValidateError> getErrors() {
		return errors;
	}
	
	/**
	 * Returns the error related target.
	 * 
	 * @return the target
	 */
	public T getTarget() {
		return target;
	}
	
	/**
	 * Clears the error list.
	 */
	public void clear() {
		errors.clear();
	}
	
	/**
	 * Checks if the result has any errors.
	 * 
	 * @return true if the result has errors
	 */
	public boolean hasErrors() {
		return !errors.isEmpty();
	}
	
	/**
	 * Checks if the supplied result list has any errors.
	 * 
	 * @param results a list with results
	 * @return true if any result has errors
	 * @param <T> the type of the validation target
	 */
	public static <T> boolean hasErrors(List<ValidateResult<T>> results) {
		
		// check all results for errors
		for (ValidateResult<T> result : results)
			if (result.hasErrors()) return true;
		
		return false;
	}
	
	/**
	 * Checks if the supplied result list has any errors related to
	 * the specified validation target.
	 * 
	 * @param results a list with results
	 * @param target the validation target to check for errors
	 * @return true if any result has errors for the target
	 * @param <T> the type of the validation target
	 */
	public static <T> boolean hasErrors(List<ValidateResult<T>> results, 
			T target) {
		
		// check target specific results for errors
		return !errorsForTarget(results, target).isEmpty();
	}
	
	/**
	 * Returns all results for the specified validation target
	 * from the supplied list.
	 * 
	 * @param results a list with results
	 * @param target the validation target to get results for
	 * @return a list of validation results for the specified target
	 * @param <T> the type of the validation target
	 */
	public static <T> List<ValidateResult<T>> forTarget(
			List<ValidateResult<T>> results, T target) {
		
		List<ValidateResult<T>> re = new Vector<>();
		
		// search results for specified target
		for (ValidateResult<T> result : results) 
			if (result.getTarget() == target) re.add(result);
		
		return re;
	}
	
	/**
	 * Returns all errors for the specified validation target
	 * from the supplied results list.
	 * 
	 * @param results a list with results
	 * @param target the validation target to get errors for
	 * @return a list of validation errors for the specified target
	 * @param <T> the type of the validation target
	 */
	public static <T> List<ValidateError> errorsForTarget(
			List<ValidateResult<T>> results, T target) {
		
		List<ValidateError> re = new Vector<>();
		
		// search errors for specified target
		for (ValidateResult<T> result : results) 
			if (result.hasErrors() && 
				result.getTarget() == target) re.addAll(result.getErrors());
		
		return re;
	}
}
