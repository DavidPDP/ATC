package co.edu.icesi.metrocali.atc.exceptions;

import org.springframework.lang.Nullable;

public class ATCRuntimeException extends RuntimeException{

	private static final long serialVersionUID = -3415646890355445876L;

	/**
	 * Construct a {@code NestedRuntimeException} with the specified detail message.
	 * @param msg the detail message
	 */
	public ATCRuntimeException(String msg) {
		super(msg);
	}
	
	/**
	 * Construct a {@code NestedRuntimeException} with the specified detail message
	 * and nested exception.
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public ATCRuntimeException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}
	
	/**
	 * Return the detail message, including the message from the nested exception
	 * if there is one.
	 */
	@Override
	@Nullable
	public String getMessage() {
		return ATCExceptionUtils.buildMessage(super.getMessage(), getCause());
	}
	
	/**
	 * Retrieve the innermost cause of this exception, if any.
	 * @return the innermost exception, or {@code null} if none
	 */
	@Nullable
	public Throwable getRootCause() {
		return ATCExceptionUtils.getRootCause(this);
	}
	
}
