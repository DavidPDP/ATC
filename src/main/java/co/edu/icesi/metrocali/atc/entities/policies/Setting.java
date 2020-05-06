package co.edu.icesi.metrocali.atc.entities.policies;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Setting {
	
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

	@JsonProperty("user")
	private User user;

}
