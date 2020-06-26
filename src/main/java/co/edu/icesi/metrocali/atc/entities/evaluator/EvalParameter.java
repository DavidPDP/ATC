package co.edu.icesi.metrocali.atc.entities.evaluator;

import java.sql.Timestamp;
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

	public EvalParameter(Date enableStart, Date enableEnd, String name, int value) {
		this.enableStart = enableStart;
		this.enableEnd = enableEnd;
		this.name = name;
		this.value = value;
	}
}


