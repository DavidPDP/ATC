package co.edu.icesi.metrocali.atc.constants;

public enum MOMRoute {

	Omega_Topic("/topic/omegas");
	
	private String channelValue;
	
	private MOMRoute(String channelValue) {
		this.channelValue = channelValue;
	}
	
	public String getChannelValue() {
		return this.channelValue;
	}
	
}
