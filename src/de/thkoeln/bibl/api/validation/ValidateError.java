package de.thkoeln.bibl.api.validation;

import java.util.List;
import java.util.Vector;

/**
 * An ValidateError includes a description for the failed validation
 * and the failed Values.
 * 
 * @author <a href="mailto:patrick.rogalla@th-koeln.de">Patrick Rogalla</a>
 *
 */
public class ValidateError {
	
	private String message;
	private List<Object> values;
	
	/**
	 * Initialize a new ValidateError with the supplied message
	 * 
	 * @param message the message describing the error
	 */
	public ValidateError(String message) {
		this.message = message;
	}
	
	/**
	 * Initialize a new ValidateError with the supplied message
	 * and values. The values can be used for substituting in the
	 * error message.
	 * 
	 * @param message the message describing the error
	 * @param values the values to substitute in the message
	 */
	public ValidateError(String message, Object... values) {
		
		this(message);
		
		this.values = new Vector<>();
		
		add(values);
	}
	
	/**
	 * Returns the error message.
	 * 
	 * @return the error message
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Adds a value to error message.
	 * 
	 * @param value the value to add
	 */
	public void add(Object value) {
		values.add(value);
	}
	
	/**
	 * Adds values to error message.
	 * 
	 * @param values values to add
	 */
	public void add(Object... values) {
		// add all supplied values
		for (Object val : values) 
			this.values.add(val);
	}
	
	/**
	 * Returns the list of values.
	 * 
	 * @return the list of values
	 */
	public List<Object> getValues() {
		return values;
	}
	
	/**
	 * Return the value at the index.
	 * 
	 * @param idx the index to get the value from
	 * @return the value
	 */
	public Object getValue(int idx) {
		return values.get(idx);
	}
	
	/**
	 * Return the value at the index and cast the value to
	 * the supplied type.
	 * 
	 * @param type the type to cast the value to
	 * @param idx the index to get the value from
	 * @return the casted value
	 * @param <T> the type to cast the value to
	 */
	public <T> T getValue(Class<T> type, int idx) {
		return type.cast(values.get(idx));
	}
	
	/**
	 * @return the error message with the substituted values
	 */
	@Override
	public String toString() {
		return String.format(message, values.toArray());
	}
}
