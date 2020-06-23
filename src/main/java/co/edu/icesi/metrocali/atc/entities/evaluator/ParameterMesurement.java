package co.edu.icesi.metrocali.atc.entities.evaluator;


import javax.persistence.*;


@Entity
@Table(name="t_003_measurements_parameter", schema = "aviom_eval")
@NamedQuery(name="ParameterMesurement.findAll", query="SELECT t FROM ParameterMesurement t")
public class ParameterMesurement {

    @Id
    @Column(name="id_measurements_parameter")
    //TODO: Must be a sequence.
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_parameter")
    private EvalParameter parameter;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="id_measurement")
    private Measurement measurement;

    public ParameterMesurement() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EvalParameter getParameter() {
        return parameter;
    }

    public void setParameter(EvalParameter parameter) {
        this.parameter = parameter;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }
    
}