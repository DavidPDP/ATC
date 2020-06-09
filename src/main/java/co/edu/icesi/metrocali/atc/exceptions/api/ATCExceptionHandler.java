package co.edu.icesi.metrocali.atc.exceptions.api;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
import co.edu.icesi.metrocali.atc.exceptions.bb.ExternalApiResponseException;

@ControllerAdvice
public class ATCExceptionHandler extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(ATCRuntimeException.class)
	public ResponseEntity<APIOutputMessage> handleATCException(
			ATCRuntimeException exception) {
		
		APIOutputMessage response = 
				resolveException(exception);
		
		return ResponseEntity.status(
			HttpStatus.valueOf(response.getErrorCode()))
		.body(response);
		
	}
	
	private APIOutputMessage resolveException(
			ATCRuntimeException rootException) {
		
		String ticket = "";
		String errorCode = "";
		String message = "";
		
		if(rootException instanceof ExternalApiResponseException) {
			
		}
		
		return new APIOutputMessage(
			ticket,
			LocalDateTime.now(),
			errorCode,
			message
		);
		
	}
	
}
