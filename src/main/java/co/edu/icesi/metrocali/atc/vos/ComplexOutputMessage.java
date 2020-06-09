package co.edu.icesi.metrocali.atc.vos;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

/**
 * Represents standard output messages.
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
