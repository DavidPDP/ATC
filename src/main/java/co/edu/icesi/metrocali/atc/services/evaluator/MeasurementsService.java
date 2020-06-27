package co.edu.icesi.metrocali.atc.services.evaluator;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.entities.evaluator.Measurement;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;
import co.edu.icesi.metrocali.atc.repositories.evaluator.MeasurementsRepository;



/**
 * MeasureService
 */
@Service
public class MeasurementsService {

    @Autowired
    private MeasurementsRepository measurementsRepository;

    public Optional<Measurement> saveMeasurement(double value, Variable variable) {
        Measurement measurement = new Measurement();
        measurement.setValue(value);
        measurement.setVariable(variable);
        return measurementsRepository.save(measurement);
    }

    public List<Measurement> getSortedMeasurementsBetweenDates(List<String> variablesNames,
            Date start, Date end) {
        return measurementsRepository.retrieveByVariableAndBetweenDates(variablesNames, start, end);
    }

    public List<Measurement> getFiveLastMeasurements(List<String> variablesNames) {

        return measurementsRepository.retrieveLastMeasurements(variablesNames);
    }

    public List<Measurement> getSortedMeasurementsBetweenDates(Variable variable, Date start,
            Date end) {
        return measurementsRepository.retrieveByVariableAndBetweenDates(variable, start, end);
    }

    public List<Measurement> getFiveLastMeasurements(Variable variable) {

        return measurementsRepository.retrieveLastMeasurements(variable);
    }

    public List<Measurement> saveMeasurements(List<Measurement> measurements) {
        return measurementsRepository.saveAll(measurements);
    }
}
