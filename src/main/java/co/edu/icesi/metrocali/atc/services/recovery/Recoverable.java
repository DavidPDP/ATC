package co.edu.icesi.metrocali.atc.services.recovery;

/**
 * Recoverability of a class is enabled by the class implementing the 
 * <code>co.edu.icesi.metrocali.atc.services.recovery.Recoverable</code> 
 * interface. A recoverable entity is one that when initializing 
 * the system its state will be recovered. This applies to system
 * crashes.
 * 
 * @author <a href="mailto:
 * johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
public interface Recoverable {
	
	/**
	 * Retrieves the business key that uniquely identifies 
	 * the entity in the ATC system.
	 * 
	 * @return business key
	 */
	public String getKeyEntity();
	
}
