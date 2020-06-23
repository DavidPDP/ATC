package co.edu.icesi.metrocali.atc.repositories.evaluator;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;

@Repository
public interface EvalParameterRepository extends CrudRepository<EvalParameter, Integer> {

    public <S extends EvalParameter> S save(S entity);

    public Optional<EvalParameter> findById(Integer id);

    public List<EvalParameter> findAll();

    public EvalParameter findByNameAndEnableEndIsNull(String name);

    public List<EvalParameter> findByEnableEndIsNull();

    public List<EvalParameter> findByEnableEnd(Date enableEnd);

    public List<EvalParameter> findByNameAndEnableStartGreaterThanAndEnableEndLessThan(String name, Date enableStart,
            Date enableEnd);

    public List<EvalParameter> findByNameAndEnableStartGreaterThan(String name, Date enableStart);

    public List<EvalParameter> findByNameAndEnableEndLessThan(String name, Date enableEnd);

    public List<EvalParameter> findByEnableStartGreaterThanAndEnableEndLessThan(Date enableStart, Date enableEnd);

    public List<EvalParameter> findByEnableStartGreaterThan(Date enableStart);

    public List<EvalParameter> findByEnableEndLessThan(Date enableEnd);

    public List<EvalParameter> findByName(String name);

}