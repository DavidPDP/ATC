package co.edu.icesi.metrocali.atc.entities.evaluator;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the t_003_parameters database table.
 * 
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EvalParameter {

	@JsonProperty("id")
	private Integer idParameter;

	@JsonProperty("enable_end")
	private Date enableEnd;

	@JsonProperty("enable_start")
	private Date enableStart;

	@JsonProperty("name")
	private String name;

	@JsonProperty("value")
	private double value;

	public EvalParameter(Date enableStart, Date enableEnd, String name, int value) {
		this.enableStart = enableStart;
		this.enableEnd = enableEnd;
		this.name = name;
		this.value = value;
	}
}


