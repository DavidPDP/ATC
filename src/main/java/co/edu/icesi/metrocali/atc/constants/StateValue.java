package co.edu.icesi.metrocali.atc.constants;

/**
 * Represents the possible states of an event. 
 * For more information, check the status flow of the event.
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
public enum StateValue {
	
	//Event's states ---------------------------------
	Pending, Approbing, Processing, Waiting, Verifying,
	
	//Virtual states for ATC -------------------------
	Rejected, Approved, Completed, Returned, Resolved,
	
	//Operator's states ------------------------------
	Available, Busy, Unavailable, Offline
	
}
