package co.edu.icesi.metrocali.atc.entities.events;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Category implements Recoverable{
	
	//Attributes -----------------------------------
	private Integer id;

	private Integer basePriority;
	
	private String name;
	
	private Category category;
	//----------------------------------------------

	@Override
	public String getKeyEntity() {
		return this.name;
	}
	
	//Aggregates -----------------------------------
	@JsonProperty("protocols")
	private List<Protocol> protocols = new ArrayList<>();
	//----------------------------------------------

	//Aggregates methods ---------------------------
	public void addProtocol(Protocol protocol) {
		this.protocols.add(protocol);
	}
	//----------------------------------------------
	
}