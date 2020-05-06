package co.edu.icesi.metrocali.atc.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import co.edu.icesi.metrocali.atc.constants.SourceTypes;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the information necessary to create an event.
 * 
 * <a href="https://stackoverflow.com/questions/1440952/
 * why-are-data-transfer-objects-dtos-an-anti-pattern">
 * DTO anti-pattern</a> 
 * 
 * is being applied for simplicity. It is recommended to follow 
 * a naming nomenclature that has meaning
 *  
 * (<a href="https://stackoverflow.com/questions/1724774/
 * java-data-transfer-object-naming-convention/35341664">
 * see cassiomolin's answer</a>).
 * 
 * @author <a href="mailto:johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
@Getter
@Setter
public class InEventMessage {

	private String title;
	
	private String description;
	
	@JsonProperty("source_value")
	private String sourceValue;
	
	@JsonProperty("source_type")
	private SourceTypes sourceType;
	
	@JsonProperty("category_name")
	private String categoryName;
	
	@JsonProperty("account_name")
	private String accountName;
	
	private int priority;
	
}
