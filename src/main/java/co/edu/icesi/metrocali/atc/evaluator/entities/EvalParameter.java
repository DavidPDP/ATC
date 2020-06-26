package co.edu.icesi.metrocali.atc.evaluator.entities;

import java.util.Date;
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

	private Integer idParameter;

	private Date enableEnd;

	private Date enableStart;

	private String name;

	private double value;

}
