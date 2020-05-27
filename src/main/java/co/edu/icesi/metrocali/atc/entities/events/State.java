package co.edu.icesi.metrocali.atc.entities.events;

import java.util.List;

import co.edu.icesi.metrocali.atc.services.recovery.Recoverable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class State implements Recoverable{

	private Integer id;
	
	@NonNull
	private String name;
	
	private String stateTypeName;
	
	private List<State> nextStates;
	
}
