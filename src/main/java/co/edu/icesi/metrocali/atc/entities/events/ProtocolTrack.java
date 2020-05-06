package co.edu.icesi.metrocali.atc.entities.events;

import org.springframework.lang.NonNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class ProtocolTrack {

	private Long id;
	
	@NonNull
	private Boolean done;
	
	@NonNull
	private Protocol protocol;
	
}
