package co.edu.icesi.metrocali.atc.exceptions.bb;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class BadRequestException extends BlackboxException {

	private static final long serialVersionUID = 3645278502500477705L;

	public BadRequestException(String msg) {
		super(msg);
	}
		
	public BadRequestException(@Nullable String msg,
			@Nullable Throwable cause) {
		super(msg,cause);
	}
	
	public BadRequestException(@Nullable String msg,
			@Nullable Throwable cause, HttpStatus code) {
		super(msg, cause, code);
	}
	
	
}
