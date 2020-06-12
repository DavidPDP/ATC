package co.edu.icesi.metrocali.atc.exceptions.bb;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class BlackboxException extends ExternalApiResponseException{

	private static final long serialVersionUID = 6102122432409043222L;
	
	private HttpStatus code;
	
	/**
	 * Construct a {@code BlackboxException} with the specified detail message.
	 * @param msg the detail message
	 */
	public BlackboxException(String msg) {
		super(msg);
	}

	/**
	 * Construct a {@code BlackboxException} with the specified detail message
	 * and nested exception.
	 * @param msg the detail message
	 * @param cause the nested exception
	 */
	public BlackboxException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}
	
	/**
	 * Construct a {@code BlackboxException} with the specified detail message,
	 * nested exception and the specified Http status code.
	 * @param msg the detail message
	 * @param cause the nested exception
	 * @param errorCode the Http status code
	 */
	public BlackboxException(@Nullable String msg, @Nullable Throwable cause,
			HttpStatus code) {
		super(msg, cause);
		this.code = code;
	}
	
	public HttpStatus getCode() {
		return this.code;
	}
	
}
