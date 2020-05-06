package co.edu.icesi.metrocali.atc.entities.policies;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Role {

	@JsonProperty("id")
	private Integer id;

	@JsonProperty("name")
	private String name;

}