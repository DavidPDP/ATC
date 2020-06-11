package co.edu.icesi.metrocali.atc.services.recovery;

import java.util.List;

public interface RecoveryPoint {

	public <T extends Recoverable> void recoverypoint(Class<T> type, 
			List<Recoverable> entities
	);
	
}
