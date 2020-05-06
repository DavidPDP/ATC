package co.edu.icesi.metrocali.atc;

import java.util.Timer;
import java.util.TimerTask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import co.edu.icesi.metrocali.atc.services.EventAllocation;
import co.edu.icesi.metrocali.atc.services.oprealtime.RecoveryPoint;

@SpringBootApplication
public class AtcApplication {

	public static void main(String[] args) {
		
		ConfigurableApplicationContext context = 
				SpringApplication.run(AtcApplication.class, args);
		
		//Recovery State
		RecoveryPoint recoveryPoint = context.getBean(RecoveryPoint.class);
		recoveryPoint.recoveryRealTimeStatus();
		context.getBeanFactory().destroyBean(recoveryPoint);
		//--------------
		
		//Timer Task
		EventAllocation task = context.getBean(EventAllocation.class);
		new Timer().schedule((TimerTask) task, 0,5000);
		//--------------
		
	}

}
