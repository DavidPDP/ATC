package co.edu.icesi.metrocali.atc.services.recovery;

import java.util.List;

import co.edu.icesi.metrocali.atc.constants.RecoveryPrecedence;

public interface RecoveryService {

	public Class<? extends Recoverable> getType();
	
	public RecoveryPrecedence getRecoveryPrecedence();
	
	public List<Recoverable> recoveryEntities();
	
}
