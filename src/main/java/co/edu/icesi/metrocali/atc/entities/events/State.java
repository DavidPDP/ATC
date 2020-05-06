package co.edu.icesi.metrocali.atc.entities.events;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class State {

	private Integer id;
	
	@NonNull
	private String name;
	
	private String stateTypeName;
	
	private List<State> nextStates;
	
}
