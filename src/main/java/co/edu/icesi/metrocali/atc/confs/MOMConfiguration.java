package co.edu.icesi.metrocali.atc.confs;

import java.util.ServiceConfigurationError;

import javax.jms.JMSException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class MOMConfiguration {

	@Bean
    public ActiveMQConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory connectionFactory = 
        		new ActiveMQConnectionFactory();
        try {
			connectionFactory.setBrokerURL("tcp://localhost:61616");
		} catch (JMSException e) {
			throw new ServiceConfigurationError(
				"Could not connect to MOM");
		}
        connectionFactory.setUser("aviom");
        connectionFactory.setPassword("metrocali");
        
        return connectionFactory;
    }
	
	@Bean
    public JmsTemplate topicTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setPubSubDomain(true);
        template.setMessageConverter(jacksonJmsMessageConverter());
        return template;
    }
	
	/**
	 * Serialize message content to json using TextMessage
	 * @return
	 */
	private MessageConverter jacksonJmsMessageConverter() {
	    MappingJackson2MessageConverter converter = 
	    		new MappingJackson2MessageConverter();
	    converter.setTargetType(MessageType.TEXT);
	    converter.setTypeIdPropertyName("_type");
	    return converter;
	}
	
	@Bean
    public JmsTemplate queueTemplate(){
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setMessageConverter(jacksonJmsMessageConverter());
        return template;
    }
	
}
