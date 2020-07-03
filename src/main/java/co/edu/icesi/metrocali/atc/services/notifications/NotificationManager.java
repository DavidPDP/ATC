package co.edu.icesi.metrocali.atc.services.notifications;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.MOMRoute;
import co.edu.icesi.metrocali.atc.constants.NotificationType;
import co.edu.icesi.metrocali.atc.services.notifications.events.EventStateChangeConcerner;
import co.edu.icesi.metrocali.atc.vos.ComplexOutputMessage;
import co.edu.icesi.metrocali.atc.vos.ExternalConcerner;
import co.edu.icesi.metrocali.atc.vos.StateNotification;

/**
 * Is responsible of making the communications between 
 * the system's clients and back-end servers through broker 
 * pattern.
 */
@Service
public class NotificationManager 
	implements EventStateChangeConcerner{
	
	/**
	 * key = eventType + accountName
	 */
	private Map<String, ExternalConcerner> unicastChannels;
	
	/**
	 * key = eventType, value = topics
	 */
	private Map<NotificationType, List<MOMRoute>> multicastChannels;
	
	private CommunicationDialect communicationDialect;
	
	public NotificationManager(
		CommunicationDialect communicationDialect) {
		
		this.communicationDialect = communicationDialect;
		
		unicastChannels = new HashMap<>();
		multicastChannels = new HashMap<>();
		
		List<MOMRoute> routes = new ArrayList<MOMRoute>();
		routes.add(MOMRoute.Omega_Topic);
		
		multicastChannels.put(
			NotificationType.New_Available_Controller, 
			routes
		);
		
		multicastChannels.put(
			NotificationType.New_Event_Assignment, 
			routes
		);
		
		multicastChannels.put(
			NotificationType.New_Event_Entity, 
			routes
		);

		multicastChannels.put(
			NotificationType.New_Measurements_Calculated, routes
		);
		
	}
	
	@Override
	public void update(StateNotification notification) {
		notify(notification);
	}
	
	private void notify(StateNotification notification) {
		tryMulticastNotify(notification);
		tryUnicastNotify(notification);
	}
	
	private void tryMulticastNotify(StateNotification notification) {
		
		List<MOMRoute> channels = 
				multicastChannels.get(notification.getType());
		
		if(channels != null) {
			
			for (MOMRoute channel : channels) {
				
				ComplexOutputMessage output = 
						new ComplexOutputMessage();
				
				output.addField("cause", 
					notification.getType().name());
				output.addField("content", 
					notification.getElementsInvolved());
				
				communicationDialect.sendToTopic(
					channel.getChannelValue(), 
					output
				);
				
			}
			
		}
		
	}
	
	private void tryUnicastNotify(StateNotification notification) {
		
		if(notification.getAddressee().isPresent()) {
			
			String channelKey = notification.getAddressee() 
				+ notification.getType().name(); 
			
			ExternalConcerner concerner = 
					unicastChannels.get(channelKey);
			
			communicationDialect.sendToQueue(
				concerner.getId(), 
				notification.getElementsInvolved()
			);
			
		}
		
	}	
	
	public void subscribe(ExternalConcerner externalConcerner) {
		
		NotificationType[] concerns = 
			externalConcerner.getConcerns();
		
		for (NotificationType concern : concerns) {
			
			String channelKey = 
				externalConcerner.getAccountName() + concern;
			
			unicastChannels.put(channelKey, externalConcerner);
			
		}
		
	}
	
	public void unsubscribe(ExternalConcerner externalConcerner) {
		
	}
	
	public void linkUp(NotificationType type, MOMRoute route) {
		
		multicastChannels.computeIfAbsent(
			type, r -> new ArrayList<>()
		).add(route);
		
	}
	
	public void disconnect(NotificationType type, MOMRoute route) {
		
	}
	
}
