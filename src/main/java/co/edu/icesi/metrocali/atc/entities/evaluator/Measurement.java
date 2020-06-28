package co.edu.icesi.metrocali.atc.entities.evaluator;



import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
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

	@JsonProperty("id")
	private Integer idMeasurement;

	@JsonProperty("end_date")
	private Date endDate;

	@JsonProperty("value")
	private double value;

	@JsonProperty("start_date")
	private Date startDate;

	@JsonProperty("variable")
	@JsonInclude(value = Include.NON_NULL)
	private Variable variable;

}
