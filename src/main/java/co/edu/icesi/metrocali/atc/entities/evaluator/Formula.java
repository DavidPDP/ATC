package co.edu.icesi.metrocali.atc.entities.evaluator;

import java.util.Date;
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

	private Integer idFormula;

	private Date endDate;

	private String expression;

	private Date startDate;

	private Variable variable;

}