package co.edu.icesi.metrocali.atc.entities.policies;

import java.sql.Timestamp;

import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Setting implements Recoverable{

	private Integer id;

	private Timestamp creation;

	private String key;

	private String type;

	private String value;

	private String version;
	
}
