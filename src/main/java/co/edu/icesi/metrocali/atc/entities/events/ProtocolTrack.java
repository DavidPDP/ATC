package co.edu.icesi.metrocali.atc.entities.events;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProtocolTrack {

	//Constructors ---------------------------------
	public ProtocolTrack() {}
	
	public ProtocolTrack(Boolean done, Protocol protocol) {
		
		this.done = done;
		this.protocol = protocol;
		
	}
	//----------------------------------------------
	
	//Attributes -----------------------------------
	private Long id;
	
	private Boolean done;
	
	private Protocol protocol;
	//----------------------------------------------
	
}
