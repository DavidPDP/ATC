package co.edu.icesi.metrocali.atc.services.notifications;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class JMSCommunication implements CommunicationDialect{

	private JmsTemplate topicTemplate;
	
	private JmsTemplate queueTemplate;
	
	public JMSCommunication(
		@Qualifier("topicTemplate") JmsTemplate topicTemplate,
		@Qualifier("queueTemplate") JmsTemplate queueTemplate) {
		
		this.topicTemplate = topicTemplate;
		this.queueTemplate = queueTemplate;
		
	}
	
	@Override
	public void sendToTopic(String topic, Object message) {
		topicTemplate.convertAndSend(topic, message);
	}

	@Override
	public void sendToQueue(String queue, Object message) {
		queueTemplate.convertAndSend(queue, message);
	}

}
