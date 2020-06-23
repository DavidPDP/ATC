package co.edu.icesi.metrocali.atc.evaluator.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the t_003_parameters database table.
 * 
 */
@Entity
@Table(name="t_003_parameters", schema = "aviom_eval")
@NamedQuery(name="EvalParameter.findAll", query="SELECT t FROM EvalParameter t")
public class EvalParameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_parameter")
	//TODO: Must be a sequence.
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idParameter;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="enable_end")
	private Date enableEnd;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="enable_start")
	private Date enableStart;
	@Column(name="name")
	private String name;
	@Column(name="value")
	private double value;



	//bi-directional many-to-one association to T003Measurement
	@OneToMany(mappedBy="parameter")
	private List<ParameterMesurement> measurements;

	public EvalParameter( Date enableStart, Date enableEnd, String name, double value) {
		this.enableStart=enableStart;
		this.enableEnd=enableEnd;
		this.name=name;
		this.value=value;
	}	

	public EvalParameter(){

	}

	public Integer getIdParameter() {
		return this.idParameter;
	}

	public void setIdParameter(Integer idParameter) {
		this.idParameter = idParameter;
	}

	public Date getEnableEnd() {
		return this.enableEnd;
	}

	public void setEnableEnd(Date enableEnd) {
		this.enableEnd = enableEnd;
	}

	public Date getEnableStart() {
		return this.enableStart;
	}

	public void setEnableStart(Date enableStart) {
		this.enableStart = enableStart;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(Double val) {
		this.value = val;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name=name;
	}

	public List<ParameterMesurement> getMeasurements() {
		return this.measurements;
	}

	public void setMeasurements(List<ParameterMesurement> measurements) {
		this.measurements = measurements;
	}

	public ParameterMesurement addMeasurement(ParameterMesurement measurement) {
		getMeasurements().add(measurement);
		measurement.setParameter(this);

		return measurement;
	}

	public ParameterMesurement removeMeasurement(ParameterMesurement measurement) {
		getMeasurements().remove(measurement);
		measurement.setParameter(null);

		return measurement;
	}

}