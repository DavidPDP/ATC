package co.edu.icesi.metrocali.atc.vos;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

/**
 * Represents the standardized output for output messages 
 * consisting of sets of different entities or information 
 * that is not encapsulated in the entities.
 * 
 * @author <a href="mailto:
 * johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
public class ComplexOutputMessage {

	private Map<String, Object> output = new HashMap<>();
	
	public void addField(String name, Object value) {
		output.put(name, value);
	}
	
	@JsonAnyGetter
	public Map<String, Object> getOutput(){
		return output;
	}
	
}
