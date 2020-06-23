package co.edu.icesi.metrocali.atc.entities.evaluator;


import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the t_003_measurements database table.
 * 
 */
@Entity
@Table(name="t_003_formulas", schema = "aviom_eval" )
@NamedQuery(name="Formula.findAll", query="SELECT t FROM Formula t")
public class Formula implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="id_formula")
	//TODO: Must be a sequence.
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer idFormula;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	private Date endDate;

    @Column(name="formula_expression")
	private String expression;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date")
	private Date startDate;


	//bi-directional many-to-one association to T003Variable
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="variable_name")
	private Variable variable;

	public Formula() {
	}

	public Integer getIdFormula() {
		return this.idFormula;
	}

	public void setIdFormula(Integer idFormula) {
		this.idFormula = idFormula;
	}

	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date eDate) {
		this.endDate = eDate;
	}

	public String getFormulaExpression() {
		return this.expression;
	}

	public void setFormulaExpression(String expression) {
		this.expression = expression;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setSartDate(Date sDate) {
		this.startDate = sDate;
	}


	public Variable getVariable() {
		return this.variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

}