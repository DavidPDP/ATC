package co.edu.icesi.metrocali.atc.entities.evaluator;



import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Variable {

	@JsonProperty(value = "name")
	private String nameVariable;

	@JsonProperty(value = "classification")
	private String classification;

	@JsonProperty(value = "description")
	private String descriptionVar;

	@JsonProperty(value = "is_kpi")
	private Boolean isKPI;

	@JsonProperty(value = "formulaExpression")
	private String lastFormulaExpression;

}
