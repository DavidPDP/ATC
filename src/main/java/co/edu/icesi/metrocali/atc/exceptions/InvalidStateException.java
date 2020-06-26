package co.edu.icesi.metrocali.atc.exceptions;

import org.springframework.lang.Nullable;

public class InvalidStateException extends ATCRuntimeException {

	private static final long serialVersionUID = -9131040798755836724L;

	public InvalidStateException(String msg) {
		super(msg);
	}
	
	public InvalidStateException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg, cause);
	}

}
