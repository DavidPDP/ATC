package co.edu.icesi.metrocali.atc.evaluator.entities;



import java.util.Date;
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
public class Measurement {

	private Integer idMeasurement;

	private Date endDate;

	private double value;

	private Date startDate;

	private Variable variable;

}
