package co.edu.icesi.metrocali.atc.services.evaluator;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;
import co.edu.icesi.metrocali.atc.entities.evaluator.Measurement;
import co.edu.icesi.metrocali.atc.entities.evaluator.ParameterMesurement;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;
import co.edu.icesi.metrocali.atc.repositories.evaluator.MeasurementRepository;


/**
 * MeasureService
 */
@Service
public class MeasurementsService {

    @Autowired
    private MeasurementRepository repository;

    public void saveMeasurement(double value, Variable var, Timestamp start, Timestamp end,
            List<EvalParameter> parameters) {
        Measurement measurement = new Measurement();
        measurement.setEndDate(end);
        measurement.setValue(value);
        measurement.setStartDate(start);
        measurement.setVariable(var);
        repository.save(measurement);

        for (EvalParameter parameter : parameters) {
            ParameterMesurement parameterMesurement = new ParameterMesurement();
            parameterMesurement.setMeasurement(measurement);
            parameterMesurement.setParameter(parameter);
            measurement.addParamenter(parameterMesurement);
        }

        repository.save(measurement);
    }

    // TODO: Change types: Timestamp to java.util.Date
    public List<Measurement> getMeasurements(Variable variable, Timestamp start, Timestamp end) {

        return repository.findByVariableAndStartDateBetween(variable, start, end);
    }

    public List<Measurement> getSortedMeasurementsByEndDate(Variable variable, Date start,
            Date end) {
        return repository.findByVariableAndStartDateGreaterThanAndEndDateLessThanOrderByEndDateDesc(variable,start, end);
    }

    public List<Measurement> getFiveLastMeasurements(Variable variable) {
        return repository.findTop5ByVariableOrderByEndDateDesc(variable);
    }
}
