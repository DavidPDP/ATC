package co.edu.icesi.metrocali.atc.services.recovery;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RecoveryManager {
	
	private List<RecoveryPoint> recoveryPoints;
	
	private List<RecoveryService> recoveryServices;
	
	public RecoveryManager(
			List<RecoveryPoint> recoveryPoints,
			List<RecoveryService> recoveryServices) {
		
		this.recoveryPoints = recoveryPoints;
		this.recoveryServices = recoveryServices;
		
	}
	
	public void recoveryRealTimeStatus(){
		
		sortByRecoveryPrecedence(recoveryServices);
		
		for (RecoveryPoint rp : recoveryPoints) {
			
			rp.preRecovery();
			
			for (RecoveryService rs : recoveryServices) {
				rp.recovery(rs.getType(), rs.recoveryEntities());
			}
			
			rp.postRecovery();
			
		}
		
	}
	
	
	private void sortByRecoveryPrecedence(
		List<RecoveryService> recoveryServices) {
		
		Comparator<RecoveryService> precedenceComparator = 
			new Comparator<RecoveryService>() {
			
			@Override
			public int compare(
				RecoveryService rs1, RecoveryService rs2) {
				return 
					rs1.getRecoveryPrecedence().getLevel() 
						- rs2.getRecoveryPrecedence().getLevel();
			}
			
		};
		
		recoveryServices.sort(precedenceComparator);
		
	}
}
