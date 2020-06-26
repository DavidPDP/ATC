package co.edu.icesi.metrocali.atc.services.notifications;

import co.edu.icesi.metrocali.atc.vos.StateNotification;

public abstract interface StateChangeConcerner {

	public void update(StateNotification notification);
	
}
