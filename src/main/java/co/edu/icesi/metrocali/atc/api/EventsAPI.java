package co.edu.icesi.metrocali.atc.api;

import co.edu.icesi.metrocali.atc.dtos.InEventMessage;

/**
 * Represents the event API. Responsible for handling 
 * requests to event services. The API can be implemented in 
 * multiple technologies, which is why mandatory services are 
 * defined. It is recommended to think about communication based 
 * on gRPC, SSE/Websocket and the HTTP/2 protocol. For more 
 * information (see Effective Java, by Josh Bloch pp. 213).
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
public interface EventsAPI {
	//TODO create a generic response wrapper to give meaning to API responses.
	public Object createEvent(InEventMessage message);
	
	public Object retrieveAssignedEvents(String accountName);
	
	public Object retrieveActiveEvents(String accountName);
	
	public Object acceptEvent(String accountName, String eventCode);
	
	public Object rejectEvent(String accountName, String eventCode);
	
	public Object sendBackEvent(String accountName, String eventCode);
	
	public Object completeEvent(String accountName, String eventCode);
	
}
