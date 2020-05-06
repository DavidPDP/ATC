package co.edu.icesi.metrocali.atc.entities.events;

import java.sql.Timestamp;

import org.springframework.lang.NonNull;

import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class EventRemarks {

	private Long id;
	
	private String code;

	@NonNull
	private String content;

	private Timestamp creation;

	@NonNull
	private User user;
	
}