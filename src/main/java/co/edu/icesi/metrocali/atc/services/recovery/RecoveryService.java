package co.edu.icesi.metrocali.atc.services.recovery;

import java.util.List;

import co.edu.icesi.metrocali.atc.constants.RecoveryPrecedence;

/**
 * 
 * 
 * @author <a href="mailto:
 * johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
public interface RecoveryService {

	public Class<? extends Recoverable> getType();
	
	public RecoveryPrecedence getRecoveryPrecedence();
	
	public List<Recoverable> recoveryEntities();
	
}
