package co.edu.icesi.metrocali.atc.exceptions.bb;

import org.springframework.lang.Nullable;

import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;

public class BadRequestException extends ATCRuntimeException {

	private static final long serialVersionUID = 3645278502500477705L;

	public BadRequestException(String msg) {
		super(msg);
	}
		
	public BadRequestException(@Nullable String msg,
			@Nullable Throwable cause) {
		super(msg,cause);
	}
	
	
}
