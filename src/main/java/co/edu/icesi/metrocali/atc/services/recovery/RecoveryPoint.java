package co.edu.icesi.metrocali.atc.services.recovery;

import java.util.List;

/**
 * Grants behavior to classes that require recovery after 
 * failure. This behavior includes, that the recovery 
 * component gives the latest system state, so that each 
 * recovery point specifically implements its recovery.
 * 
 * @author <a href="mailto:
 * johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
public interface RecoveryPoint {

	/**
	 * Is used as a callback notification to signal that 
	 * recovery process in this recovery point started. 
	 * Implement this to perform pre-recovery or any initialization 
	 * processing of the recovery point.
	 */
	public void preRecovery();
	
	/**
	 * Concrete implementation of the recovery using the 
	 * latest system state. The last state is represented 
	 * by a list of recoverable entities, which are passed 
	 * by parameter using the recovery component.
	 * 
	 * @param type entity type contained in the list.
	 * @param entities entity list.
	 */
	public <T extends Recoverable> void recovery(Class<T> type, 
			List<Recoverable> entities
	);
	
	/**
	 * Is used as a callback notification to signal that 
	 * recovery process in this recovery point finished. 
	 * Implement this to perform post-recovery or any initialization 
	 * processing of the recovery point.
	 */
	public void postRecovery();
	
}
