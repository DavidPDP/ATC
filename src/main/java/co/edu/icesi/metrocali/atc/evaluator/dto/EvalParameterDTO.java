package co.edu.icesi.metrocali.atc.evaluator.dto;


import java.util.Date;
import co.edu.icesi.metrocali.atc.evaluator.entities.EvalParameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvalParameterDTO {
    

    private Integer id;
    private String name;
    private double value;
    private Date enableStart;
    private Date enableEnd;

    public EvalParameterDTO(EvalParameter parameter){
        this.id=parameter.getIdParameter();
        this.name = parameter.getName();
        this.value=parameter.getValue();
        this.enableStart = parameter.getEnableStart();
        this.enableEnd=parameter.getEnableEnd();
    }

}