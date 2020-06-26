package co.edu.icesi.metrocali.atc.services.notifications;

import co.edu.icesi.metrocali.atc.vos.StateNotification;

/**
 * Weak references
 * https://stackoverflow.com/questions/6337760/pros-and-cons-of-listeners-as-weakreferences
 *
 */
public abstract interface StateChangeGenerator {
	
	public void attach(StateChangeConcerner stateChangeConcerner);
	
	public void detach(StateChangeConcerner stateChangeConcerner);
	
	public void notifyNewStateChange(StateNotification notification);
	
}
