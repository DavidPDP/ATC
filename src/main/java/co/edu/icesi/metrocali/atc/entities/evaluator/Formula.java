package co.edu.icesi.metrocali.atc.entities.evaluator;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Formula {

	@JsonProperty("id")
	private Integer idFormula;

	@JsonProperty("end_date")
	private Date endDate;

	@JsonProperty("formula_expression")
	private String expression;

	@JsonProperty("start_date")
	private Date startDate;

	@JsonProperty("variable")
	private Variable variable;

}