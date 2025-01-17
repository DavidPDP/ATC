package co.edu.icesi.metrocali.atc.exceptions.bb;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.UnknownHttpStatusCodeException;

//import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class BlackboxExceptionHandler 
	extends DefaultResponseErrorHandler {
	
	@Override
	public void handleError(ClientHttpResponse response) 
			throws IOException {
		
		try {
			super.handleError(response);
		}catch(HttpClientErrorException e) {
			
			ExternalApiResponseException newException = null;
			
			if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				newException = new ResourceNotFound(
					"the resource is not persisted.",
					e, e.getStatusCode()
				);
			}else {
				newException = new BadRequestException(
					"the request could not be completed.", 
					e, e.getStatusCode()
				);
			}
			
			//log.error("", newException);
			throw newException;
			
		}catch(HttpServerErrorException e) {
			
			BlackboxException newException = 
				new BlackboxException(
					"An error has occurred in the persistence "
					+ "layer, contact the admin and request an "
					+ "explanation of the following ticket "
					+ "or see the trace", e, e.getStatusCode()
			);
			
			//log.error("", newException);
			throw newException;
			
		}catch(UnknownHttpStatusCodeException e) {
			
			BlackboxException newException = 
				new BlackboxException(
					"An error has occurred in the persistence "
					+ "layer with an unknown error code. "
					+ "Contact the admin with the ticket "
					+ "number or see the trace.", e);
			
			//log.error("", newException);
			throw newException;
			
		}
		
	}
	
}
