package co.edu.icesi.metrocali.atc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import co.edu.icesi.metrocali.atc.services.recovery.RecoveryManager;

@SpringBootApplication
public class AtcApplication {

	public static void main(String[] args) {
		
		ConfigurableApplicationContext context = 
			SpringApplication.run(AtcApplication.class, args);
		
		//Recovery State -------------------------------
		RecoveryManager recoveryPoint = 
			context.getBean(RecoveryManager.class);
		recoveryPoint.recoveryRealTimeStatus();
		context.getBeanFactory().destroyBean(recoveryPoint);
		//----------------------------------------------
		
	}

}
