package co.edu.icesi.metrocali.atc.evaluator.entities;



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

	private String nameVariable;

	private String classification;

	private String descriptionVar;

	private Boolean isKPI;

}
