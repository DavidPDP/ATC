package co.edu.icesi.metrocali.atc.services.notifications;

public interface CommunicationDialect {

	public void sendToTopic(String topic, Object message);
	
	public void sendToQueue(String queue, Object message);
	
}
