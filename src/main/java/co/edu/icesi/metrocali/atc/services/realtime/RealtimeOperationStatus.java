package co.edu.icesi.metrocali.atc.services.realtime;

import java.util.List;
import java.util.Optional;

import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;

/**
 * Interface for generic operations on temporary storage in 
 * RAM for system entities.
 *
 * @author <a href="mailto:
 * johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
public interface RealtimeOperationStatus {
	
	public <T extends Recoverable> void store(
		Class<T> type, T entity);
	
	public <T extends Recoverable> void store(
		Class<T> type, List<T> entities);
	
	public <T extends Recoverable, V extends Recoverable> 
		void storeToList(Class<T> keyType, T keyEntity, 
				Class<V> valueType, V valueEntity);
	
	public <T extends Recoverable> List<T> 
		retrieveAll(Class<T> type);
	
	public <T extends Recoverable> Optional<T> retrieve(
		Class<T> type, String entityKey);
	
	public <T extends Recoverable> T remove(
		Class<T> type, String entityKey);
	
	public <T extends Recoverable> List<T> filter(
		Class<T> type, String ... filtersValue);
	
}
