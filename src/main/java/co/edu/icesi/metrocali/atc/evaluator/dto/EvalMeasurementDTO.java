package co.edu.icesi.metrocali.atc.evaluator.dto;

import java.util.Date;
import co.edu.icesi.metrocali.atc.evaluator.entities.Measurement;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EvalMeasurementDTO {

    private String variable;
    private double value;
    private Date startDate;
    private Date endDate;

    public EvalMeasurementDTO(Measurement measurement) {
        this.variable = measurement.getVariable().getNameVariable();
        this.value = measurement.getValue();
        this.startDate = measurement.getStartDate();
        this.endDate = measurement.getEndDate();
    }
}
