package co.edu.icesi.metrocali.atc.vos;

import lombok.Value;

/**
 * Represents the stateless token that is generated for 
 * claiming requests by the user. For more information 
 * consult RFC 7519.
 * 
 * @author <a href="mailto:
 * johan.ballesteros@outlook.com">Johan Ballesteros</a>
 */
@Value
public class SessionToken {

	private String token;
	
}
