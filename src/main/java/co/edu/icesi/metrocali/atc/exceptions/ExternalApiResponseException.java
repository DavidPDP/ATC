package co.edu.icesi.metrocali.atc.exceptions;

import org.springframework.lang.Nullable;

public class ExternalApiResponseException extends ATCRuntimeException {

	private static final long serialVersionUID = 1572966168074814035L;

	public ExternalApiResponseException(String msg) {
		super(msg);
	}
		
	public ExternalApiResponseException(@Nullable String msg, @Nullable Throwable cause) {
		super(msg,cause);
	}
}
