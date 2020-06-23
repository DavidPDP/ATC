package co.edu.icesi.metrocali.atc.services.evaluator;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;
import co.edu.icesi.metrocali.atc.repositories.evaluator.EvalParameterRepository;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EvalParametersService {

    public static final String PERIODICITY_NAME = "PERIODICIDAD";

    /**
     * It represents a default peridocity parameter with -1 as its value. It is used system never
     * has could query to DB or if the periodicity parameter at DB does not exist.
     */
    public static final EvalParameter NON_PERIODICITY = new EvalParameter(
            new Timestamp(System.currentTimeMillis()), null, PERIODICITY_NAME, -1);

    @Autowired
    private EvalParameterRepository parameterRepository;

    private EvalParameter parameter;

    @PostConstruct
    private void setupPeriodicity() {
        this.parameter = getDBPeriodicity();
    }

    /**
     * EvalParameterService saves and returns a local EvalParameter which represents the last
     * queried periodicity from DB.
     * 
     * @return the value of the current EvalParameter at the service. It could be -1 if the query to
     *         DB has failed, in other words, it will be the value of {@link #NON_PERIODICITY}
     * 
     */
    public double getLocalPeriodicity() {
        return this.parameter.getValue();
    }

    /**
     * Get periodicity from DB. If an expcetion is throwed the DBperiodicity will be
     * {@link #DEFAULT_PERIODICITY_IN_MINS} or will be the current value of {@link #periodicity} if
     * it is not {@link #NON_PERIODICITY}
     */
    private EvalParameter getDBPeriodicity() {
        EvalParameter DBperiodicity = this.parameter;

        try {
            DBperiodicity = parameterRepository.findByNameAndEnableEndIsNull(PERIODICITY_NAME);
            if (DBperiodicity == null) {
                DBperiodicity = NON_PERIODICITY;
            }

        } catch (final Exception e) {
            log.error("Parameters error. Default periodicity will be used", e);
            DBperiodicity = NON_PERIODICITY;
        }
        return DBperiodicity;
    }

    /**
     * if {@link #periodicity} has changed, its value will be updated using
     * {@link #getDBPeriodicity()}.
     */

    public boolean periodicityHasChanged() {
        final EvalParameter DBPeriodicity = getDBPeriodicity();
        final double DBperiodicityValue = DBPeriodicity.getValue();
        final boolean hasChanged = this.parameter.getValue() != DBperiodicityValue;
        if (hasChanged) {
            this.parameter = DBPeriodicity;
            log.debug("DB's periodicity has changed", this.parameter.getValue());
        }
        return hasChanged;
    }

    public EvalParameter getCurrentParameter() {
        return parameter;
    }

    public List<EvalParameter> getParameters(String name, Date enableStart, Date enableEnd) {
        List<EvalParameter> parameters = null;
        if (name != null && !name.isEmpty()) {
            if (enableStart != null) {
                if (enableEnd != null) {
                    parameters = parameterRepository
                            .findByNameAndEnableStartGreaterThanAndEnableEndLessThan(name,
                                    enableStart, enableEnd);
                } else {
                    parameters = parameterRepository.findByNameAndEnableStartGreaterThan(name,
                            enableStart);
                }
            } else if (enableEnd != null) {
                parameters = parameterRepository.findByNameAndEnableEndLessThan(name, enableEnd);
            } else {
                parameters = parameterRepository.findByName(name);
            }
        } else {
            if (enableStart != null) {
                if (enableEnd != null) {
                    parameters =
                            parameterRepository.findByEnableStartGreaterThanAndEnableEndLessThan(
                                    enableStart, enableEnd);
                } else {
                    parameters = parameterRepository.findByEnableStartGreaterThan(enableStart);
                }
            } else if (enableEnd != null) {
                parameters = parameterRepository.findByEnableEndLessThan(enableEnd);
            } else {
                parameters = parameterRepository.findAll();
            }
        }
        return parameters;
    }

    public void updateParameterValue(String parameterName, Double value) throws Exception {
        Timestamp nowDate = new Timestamp(System.currentTimeMillis());

        EvalParameter oldParameter = null;
        try {
            oldParameter = parameterRepository.findByNameAndEnableEndIsNull(parameterName);
            if (oldParameter == null) {
                throw new IllegalArgumentException(
                        "El parámetro no existe o no hay un parámetro con este nombre que este actualmente disponible");
            }
            // NOTE: change oldParameter updates DB
            oldParameter.setEnableEnd(nowDate);
        } catch (Exception e) {
            throw e;
        }

        try {
            EvalParameter newParameter = new EvalParameter();
            newParameter.setName(parameterName);
            newParameter.setEnableStart(nowDate);
            newParameter.setValue(value);
            parameterRepository.save(newParameter);
        } catch (Exception e) {
            if (oldParameter != null) {
                oldParameter.setEnableEnd(null);
            }
            throw e;
        }

    }

    public List<EvalParameter> getActiveParameters() {
      return parameterRepository.findByEnableEndIsNull();
    }
}
