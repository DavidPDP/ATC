//package co.edu.icesi.metrocali.atc.utils;
//
//import java.io.IOException;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.fasterxml.jackson.databind.ser.std.StdSerializer;
//
//import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
//
//public class EventTrackSerializer extends StdSerializer<EventTrack>{
//
//	private static final long serialVersionUID = 2500636898169572371L;
//
//	public EventTrackSerializer() {
//		this(null);
//	}
//	
//	protected EventTrackSerializer(Class<EventTrack> t) {
//		super(t);
//	}
//
//	@Override
//	public void serialize(EventTrack value, JsonGenerator gen, 
//			SerializerProvider provider) throws IOException {
//		
//		gen.writeStartObject();
//		
//		if(value.getId() != null) {
//			gen.writeNumberField("id", value.getId());
//		}
//		
//		if(value.getStartTime() != null) {
//			gen.writeObjectField("startTime", value.getStartTime());
//		}
//		
//		if(value.getEndTime() != null) {
//			gen.writeObjectField("endTime", value.getEndTime());
//		}
//
//		gen.writeNumberField("priority", value.getPriority());
//		
//		if(value.getEventId() != null) {
//			gen.writeObjectFieldStart("event");
//			gen.writeNumberField("id", value.getEventId());
//			gen.writeEndObject();
//		}
//		
//		gen.writeObjectFieldStart("user");
//		gen.writeNumberField("id", value.getUser().getId());
//		gen.writeEndObject();
//		
//		gen.writeObjectFieldStart("state");
//		gen.writeNumberField("id", value.getState().getId());
//		gen.writeEndObject();
//		
//		gen.writeEndObject();
//	}
//	
//}
