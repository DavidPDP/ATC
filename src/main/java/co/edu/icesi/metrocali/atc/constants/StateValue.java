package co.edu.icesi.metrocali.atc.constants;

/**
 * Represents the possible states of an event. 
 * For more information, check the status flow of the event.
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
public enum StateValue {
	
	Pending, Assigned, In_Proccess, On_Hold,
	Verification, Archived,
	
	Available, Busy, Unavailable, Offline
	
}
