package co.edu.icesi.metrocali.atc.services.evaluator;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;
import co.edu.icesi.metrocali.atc.repositories.evaluator.EvalParametersRepository;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class EvalParametersService {

    public static final String PERIODICITY_NAME = "PERIODICIDAD";

    /**
     * It represents a default peridocity parameter with -1 as its value. It is used system never
     * has could query to DB or if the periodicity parameter at DB does not exist.
     */
    public static final EvalParameter NON_PERIODICITY =
            new EvalParameter(Date.from(new Timestamp(System.currentTimeMillis()).toInstant()),
                    null, PERIODICITY_NAME, -1);

    @Autowired
    private EvalParametersRepository parameterRepository;

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
            Optional<EvalParameter> DBPeriodicityWrapper =
                    parameterRepository.retrieveActiveByName(PERIODICITY_NAME);

            if (!DBPeriodicityWrapper.isPresent()) {
                DBperiodicity = NON_PERIODICITY;
            } else {
                DBperiodicity = DBPeriodicityWrapper.get();
            }

        } catch (Exception e) {
            log.error("Parameters request error. Default periodicity will be used", e);
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

    public List<EvalParameter> getParameters(final String name, final Date enableStart,
            final Date enableEnd) {
        List<EvalParameter> parameters = null;
        if (name != null && !name.isEmpty()) {
            if (enableStart != null && enableEnd != null) {

                parameters = parameterRepository.retrieveByName(name, enableStart, enableEnd);
            } else {
                parameters = parameterRepository.retrieveByName(name);
            }
        } else if (enableStart != null && enableEnd != null) {
            parameters = parameterRepository.retrieveBetweenDates(enableStart, enableEnd);
        } else {
            parameters = parameterRepository.retrieveAll();
        }
        return parameters;
    }

    public Optional<EvalParameter> updateParameterValue(final String parameterName,
            final Double value) throws Exception {
        Date currentDate = Date.from(new Timestamp(System.currentTimeMillis()).toInstant());
        EvalParameter newParameter = new EvalParameter();
        newParameter.setName(parameterName);
        newParameter.setValue(value);
        newParameter.setEnableStart(currentDate);
        newParameter.setEnableEnd(null);

        return parameterRepository.update(newParameter);
    }

    public List<EvalParameter> getActiveParameters() {
        return parameterRepository.retrieveActives();
    }
}
