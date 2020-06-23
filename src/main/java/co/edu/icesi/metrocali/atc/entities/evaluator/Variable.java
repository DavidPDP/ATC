package co.edu.icesi.metrocali.atc.entities.evaluator;



import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the t_003_variables database table.
 * 
 */
@Entity
@Table(name = "t_003_variables", schema = "aviom_eval")
@NamedQuery(name = "Variable.findAll", query = "SELECT t FROM Variable t")
public class Variable implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "name")
	private String nameVariable;

	@Column(name = "classification")
	private String classification;

	@Column(name = "description_var")
	private String descriptionVar;

	@Column(name = "is_kpi")
	private Boolean isKPI;

	// bi-directional many-to-one association to T003Measurement
	@OneToMany(mappedBy = "variable",cascade = CascadeType.REMOVE)
	private List<Measurement> measurements= new ArrayList<Measurement>();

	@OneToMany(mappedBy = "variable",cascade = CascadeType.REMOVE)
	private List<Formula> formulas= new ArrayList<Formula>();



	public Variable() {
	}

	public String getNameVariable() {
		return this.nameVariable;
	}

	public void setNameVariable(String nameVariable) {
		this.nameVariable = nameVariable;
	}

	public String getClassification() {
		return this.classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getDescriptionVar() {
		return this.descriptionVar;
	}

	public void setDescriptionVar(String descriptionVar) {
		this.descriptionVar = descriptionVar;
	}

	public Boolean getIsKPI() {
		return this.isKPI;
	}

	public void setIsKPI(boolean isKPI) {
		this.isKPI = isKPI;
	}

	public List<Formula> getFormulas() {
		return this.formulas;
	}

	public void setFormula(List<Formula> formulas) {
		this.formulas = formulas;
	}

	public List<Measurement> getMeasurements() {
		return this.measurements;
	}

	public void setMeasurements(List<Measurement> measurements) {
		this.measurements = measurements;
	}

	public Measurement addMeasurement(Measurement measurement) {
		getMeasurements().add(measurement);
		measurement.setVariable(this);

		return measurement;
	}

	public Measurement removeMeasurement(Measurement measurement) {
		getMeasurements().remove(measurement);
		measurement.setVariable(null);

		return measurement;
	}

	public Formula addFormula(Formula formula) {

		getFormulas().add(formula);
		formula.setVariable(this);

		return formula;
	}

	public Formula removeFormuña(Formula formula) {
		getFormulas().remove(formula);
		formula.setVariable(null);

		return formula;
	}

}
