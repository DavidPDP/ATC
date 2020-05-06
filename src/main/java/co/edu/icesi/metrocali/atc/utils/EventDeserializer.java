//package co.edu.icesi.metrocali.atc.utils;
//
//import java.io.IOException;
//import java.sql.Timestamp;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Iterator;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.context.support.SpringBeanAutowiringSupport;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.DeserializationContext;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
//
//import co.edu.icesi.metrocali.atc.entities.events.Category;
//import co.edu.icesi.metrocali.atc.entities.events.Event;
//import co.edu.icesi.metrocali.atc.entities.events.EventSource;
//import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
//import co.edu.icesi.metrocali.atc.entities.events.State;
//import co.edu.icesi.metrocali.atc.entities.operators.Controller;
//import co.edu.icesi.metrocali.atc.entities.policies.User;
//import co.edu.icesi.metrocali.atc.services.entities.CategoriesService;
//import co.edu.icesi.metrocali.atc.services.entities.EventsService;
//import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;
//import co.edu.icesi.metrocali.atc.vos.EventStates;
//import co.edu.icesi.metrocali.atc.vos.OperatorTypes;
//
//public class EventDeserializer extends StdDeserializer<Event> {
//
//	private static final long serialVersionUID = 7321877719116544160L;
//
//	private CategoriesService categoriesService;
//
//	private OperatorsService operatorsService;
//
//	private EventsService eventsService;
//
//	protected EventDeserializer(Class<?> vc) {
//		super(vc);
//	}
//
//	public EventDeserializer() {
//		this(Event.class);
//		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
//	}
//
//	@Autowired
//	private void setCategoriesService(CategoriesService categoriesService) {
//		this.categoriesService = categoriesService;
//	}
//
//	@Autowired
//	private void setOperatorsService(OperatorsService operatorsService) {
//		this.operatorsService = operatorsService;
//	}
//
//	@Autowired
//	private void setEventsService(EventsService eventsService) {
//		this.eventsService = eventsService;
//	}
//
//	@Override
//	public Event deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
//
//		JsonNode rootNode = p.getCodec().readTree(p);
//		JsonNode context = rootNode.get("context");
//
//		if ("front-end".equals(context.asText())) {
//			return deserializeFrontEndEvent(rootNode);
//		} else if ("black-box".equals(context.asText())) {
//			return deserializeBlackBoxEvent(rootNode);
//		} else {
//			throw new IllegalArgumentException();
//		}
//	}
//
//	private Event deserializeBlackBoxEvent(JsonNode rootNode) {
//		System.out.println(rootNode);
//		Event event = new Event();
//
//		event.setId(rootNode.get("id").asLong());
//
//		SimpleDateFormat dateFormat = 
//				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//
//		try {
//			
//			event.setCreation(
//				new Timestamp(
//					dateFormat.parse(
//						rootNode.get("creation").asText()
//					).getTime()
//				)
//			);
//			
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		event.setEventsTracks(new ArrayList<>());
//		System.out.println(rootNode.get("event_tracks"));
//		Iterator<JsonNode> eventTracks = rootNode.get("event_tracks").elements();
//		while (eventTracks.hasNext()) {
//
//			JsonNode jsonNode = eventTracks.next();
//			EventTrack eventTrack = new EventTrack();
//			eventTrack.setId(jsonNode.get("id").asLong());
//			eventTrack.setPriority(jsonNode.get("priority").asInt());
//			eventTrack.setEventId(rootNode.get("id").asLong());
//
//			try {
//				
//				eventTrack.setStartTime(
//					new Timestamp(
//						dateFormat.parse(
//							jsonNode.get("startTime").asText()
//						).getTime()
//					)
//				);
//				
//				JsonNode endTime = jsonNode.get("endTime");
//				if(endTime != null && !endTime.asText().equals("null")) {
//					
//					eventTrack.setEndTime(
//						new Timestamp(
//							dateFormat.parse(
//								endTime.asText()
//							).getTime()
//						)
//					);
//					
//				}
//				
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//
//			State state = new State();
//			state.setId(jsonNode.get("state").get("id").asInt());
//			eventTrack.setState(state);
//
//			eventTrack.setUser(
//				operatorsService.retrieveOperator(
//					jsonNode.get("user").get("accountName").asText(), 
//					OperatorTypes.Controller
//				)
//			);
//
//			event.addEventTrack(eventTrack);
//		}
//
//		return event;
//	}
//
//	private Event deserializeFrontEndEvent(JsonNode rootNode) {
//
//		EventSource eventSource = new EventSource(rootNode.get("source_value").asText(),
//				rootNode.get("source_type").asText());
//
//		Category category = categoriesService.retrieveCategory(rootNode.get("category_name").asText());
//
//		EventTrack eventTrack = new EventTrack(
//			category.getBasePriority(),
//			operatorsService.retrieveOperator(
//				rootNode.get("account_name").asText(), 
//				OperatorTypes.Controller
//			),
//			eventsService.retrieveEventState(EventStates.Created)
//		);
//
//		Event event = new Event(
//			rootNode.get("description").asText(), eventSource, 
//			rootNode.get("title").asText(),
//			category, 
//			new ArrayList<EventTrack>(Arrays.asList(eventTrack))
//		);
//
//		return event;
//
//	}
//
//}
