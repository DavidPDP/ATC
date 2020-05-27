package co.edu.icesi.metrocali.atc.services.recovery;

import java.util.List;
import java.util.Map;

public interface RecoveryPoint {

	public void recoverypoint(Map<String,
			List<? extends Recoverable>> entities);
	
}
