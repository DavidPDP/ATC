package co.edu.icesi.metrocali.atc.evaluator.repositories;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import co.edu.icesi.metrocali.atc.evaluator.entities.Measurement;
import co.edu.icesi.metrocali.atc.evaluator.entities.Variable;



@Repository
public interface MeasurementRepository extends CrudRepository<Measurement, Integer> {

        public <S extends Measurement> S save(S entity);

        public List<Measurement> findAll();

        public List<Measurement> findByVariable(Variable variable);

        public List<Measurement> findByVariableAndEndDateBetween(Variable variable, Date startDate,
                        Date endDate);

        public List<Measurement> findByVariableAndStartDateBetween(Variable var, Timestamp start,
                        Timestamp end);

        public List<Measurement> findByVariableAndStartDateGreaterThanAndEndDateLessThanOrderByEndDateDesc(
                        Variable variable, Date start, Date end);

        public List<Measurement> findTop5ByVariableOrderByEndDateDesc(Variable variable);


}
