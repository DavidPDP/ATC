package co.edu.icesi.metrocali.atc.exceptions.api;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Value;

@Value
public class APIOutputMessage {

	private String ticket;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, 
		pattern = "dd-MM-yyyy hh:mm:ss")
	private LocalDateTime timestamp;
	
	private String errorCode;
	
	private String message;
	
}
