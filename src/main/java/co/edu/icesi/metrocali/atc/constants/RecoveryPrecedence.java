package co.edu.icesi.metrocali.atc.constants;

public enum RecoveryPrecedence {

	First(1),
	Second(2),
	Third(3);
	
	private Integer level;
	
	private RecoveryPrecedence(Integer level) {
		this.level = level;
	}
	
	public Integer getLevel() {
		return this.level;
	}

}
