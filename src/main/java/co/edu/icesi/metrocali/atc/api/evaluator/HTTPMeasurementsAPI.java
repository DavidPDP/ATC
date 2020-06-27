package co.edu.icesi.metrocali.atc.api.evaluator;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import co.edu.icesi.metrocali.atc.entities.evaluator.Measurement;
import co.edu.icesi.metrocali.atc.services.evaluator.MeasurementsService;

@RestController
@RequestMapping("/evaluator/measurements")
public class HTTPMeasurementsAPI {
    @Autowired
    private MeasurementsService measurementsService;

    private HashMap<String, List<Measurement>> groupByVariable(List<Measurement> measurements) {
        HashMap<String, List<Measurement>> measurementsByVariable = new HashMap<>();
        for (Measurement measurement : measurements) {
            String variableName = measurement.getVariable().getNameVariable();
            if (measurementsByVariable.containsKey(variableName)) {
                measurementsByVariable.get(variableName).add(measurement);
            } else {
                measurementsByVariable.put(variableName, Arrays.asList(measurement));
            }
        }
        return measurementsByVariable;

    }

    private HashMap<String, List<Measurement>> getLastVariableMeasurements(
            List<String> variablesNames) throws Exception {
        HashMap<String, List<Measurement>> measurementsByVariable = new HashMap<>();


        if (variablesNames != null && variablesNames.size() > 0) {

            List<Measurement> measurements =
                    measurementsService.getFiveLastMeasurements(variablesNames);

            measurementsByVariable = groupByVariable(measurements);


            return measurementsByVariable;
        } else {
            throw new Exception("Se debe especificar, al menos, el nombre de una variable");
        }

    }

    // dashboard
    /**
     * Returns a map with measurements for the indicated variables. Each measurements group is
     * ordered in an descending way
     * 
     * @param names     a list of variable names which measurements will be obtained
     * @param startDate a date which represents the 'from' date from measurements (for each
     *                  variable) will be obtained. Is an optional parameter
     * @param endDate   a date which represents the 'until' date until measurements (for each
     *                  variable) will be obtained. Is an optional parameter.
     * @param lasts     a boolean which specify if the values to calculate will be the last five
     *                  values. It overrides the dates parameters. if its value, the service will
     *                  return the last five measurements for each variable.
     * @return a hashmap (json) with measurements for each variable. key is variable name and value
     *         is a list with measurements for that variable.
     * @see #getLastVariableMeasurements
     */
    @GetMapping
    public ResponseEntity<HashMap<String, List<Measurement>>> getVariableMeasurements(
            @RequestParam(required = true, value = "names") List<String> variablesNames,
            @RequestParam(required = false, name = "s_date") Date startDate,
            @RequestParam(required = false, name = "e_date") Date endDate,
            @RequestParam(required = false, name = "lasts") boolean lasts) throws Exception {

        HashMap<String, List<Measurement>> measurementsByVariable = new HashMap<>();

        if (variablesNames != null && variablesNames.size() > 0) {
            if (lasts) {
                measurementsByVariable = getLastVariableMeasurements(variablesNames);
            } else {
                // NOTE: Start timestamp: today
                Date start =
                        Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
                // NOTE: End timestamp: tomorrow
                Date end = Date.from(LocalDate.now().plusDays(1)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());

                if (startDate != null) {
                    start = startDate;
                }
                if (endDate != null) {
                    end = endDate;
                }


                for (String name : variablesNames) {
                    List<Measurement> measurements = measurementsService
                            .getSortedMeasurementsBetweenDates(variablesNames, start, end);
                    groupByVariable(measurements);

                    measurementsByVariable.put(name, measurements);
                }
            }
            return ResponseEntity.ok().body(measurementsByVariable);
        } else {
            throw new Exception("Se debe especificar, al menos, el nombre de una variable.");
        }


    }
}
