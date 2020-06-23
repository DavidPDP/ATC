package co.edu.icesi.metrocali.atc.entities.evaluator;



import java.io.Serializable;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the t_003_measurements database table.
 * 
 */
@Entity
@Table(name = "t_003_measurements", schema = "aviom_eval")
@NamedQuery(name = "Measurement.findAll", query = "SELECT t FROM Measurement t")
public class Measurement implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id_measurement")
	// TODO: Must be a sequence.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idMeasurement;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;

	private double value;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date")
	private Date startDate;


	// bi-directional many-to-one association to T003Variable
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "variable_name")
	private Variable variable;

	@OneToMany(mappedBy = "measurement", cascade = CascadeType.ALL)
	private List<ParameterMesurement> parameters = new ArrayList<>();

	public Measurement() {
	}

	public Integer getIdMeasurement() {
		return this.idMeasurement;
	}

	public void setIdMeasurement(Integer idMeasurement) {
		this.idMeasurement = idMeasurement;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date eDate) {
		this.endDate = eDate;
	}

	public double getValue() {
		return this.value;
	}

	public void setValue(double measure) {
		this.value = measure;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date sDate) {
		this.startDate = sDate;
	}

	public Variable getVariable() {
		return this.variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public List<ParameterMesurement> getParameters() {
		return this.parameters;
	}

	public void setMeasurements(List<ParameterMesurement> parameters) {
		this.parameters = parameters;
	}

	public ParameterMesurement addParamenter(ParameterMesurement parameter) {
		getParameters().add(parameter);
		parameter.setMeasurement(this);

		return parameter;
	}

	public ParameterMesurement removeParameter(ParameterMesurement parameter) {
		getParameters().remove(parameter);
		parameter.setParameter(null);
		return parameter;
	}
}
