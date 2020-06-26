package co.edu.icesi.metrocali.atc.exceptions.bb;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

public class ResourceNotFound extends BlackboxException {

	private static final long serialVersionUID = 6056666116606169541L;

	public ResourceNotFound(String msg) {
		super(msg);
	}
		
	public ResourceNotFound(@Nullable String msg,
			@Nullable Throwable cause) {
		super(msg,cause);
	}
	
	public ResourceNotFound(@Nullable String msg,
			@Nullable Throwable cause, HttpStatus code) {
		super(msg, cause, code);
	}
	
}
