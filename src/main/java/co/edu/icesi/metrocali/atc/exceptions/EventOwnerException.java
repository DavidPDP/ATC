package co.edu.icesi.metrocali.atc.exceptions;

import org.springframework.lang.Nullable;

public class EventOwnerException extends ATCRuntimeException{

	private static final long serialVersionUID = 9014215023366451255L;

	/**
	 * Construct a {@code EventOwnerException} with the specified detail message.
	 * @param msg the detail message
	 */
	public EventOwnerException(String msg) {
		super(msg);
	}

	/**
	 * Construct a {@code EventOwnerException} with the specified detail message
	 * and nested exception.
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public EventOwnerException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}
	
}
