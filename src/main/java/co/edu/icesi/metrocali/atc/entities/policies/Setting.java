package co.edu.icesi.metrocali.atc.entities.policies;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Setting implements Recoverable{
	
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("creation")
	private Timestamp creation;

	@JsonProperty("key")
	private String key;

	@JsonProperty("type")
	private String type;

	@JsonProperty("value")
	private String value;

	@JsonProperty("version")
	private String version;

	@JsonIgnore
	public Object getInstanceValue() {
		switch (this.type) {
		case "String":
			return value;
		case "Integer":
			return Integer.valueOf(value);
		default:
			return null;
		}
	}
	
}
