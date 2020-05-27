package co.edu.icesi.metrocali.atc.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import co.edu.icesi.metrocali.atc.constants.SecurityConstants;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

	private OperatorsService operatorsService;
	
	private TokenProvider jwtTokenUtil;
	
	@Autowired
	public TokenAuthenticationFilter(OperatorsService operatorsService,
			TokenProvider jwtTokenUtil) {
		this.operatorsService = operatorsService;
		this.jwtTokenUtil = jwtTokenUtil;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String username = null;
        String authToken = null;
		String header = request.getHeader(SecurityConstants.HEADER_STRING);
        
        if (header != null && header.startsWith(SecurityConstants.TOKEN_PREFIX)) {
        	
            authToken = header.replace(SecurityConstants.TOKEN_PREFIX,"");
            
            try {
                username = jwtTokenUtil.getUsernameFromToken(authToken);
            } catch (IllegalArgumentException e) {
                //logger.error("an error occured during getting username from token", e);
            } catch (ExpiredJwtException e) {
                //logger.warn("the token is expired and not valid anymore", e);
            } catch(SignatureException e){
                //logger.error("Authentication Failed. Username or Password not valid.");
            }
            
        } else {
            //logger.warn("couldn't find bearer string, will ignore the header");
        }
        if (username != null && 
        		SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = operatorsService.loadUserByUsername(username);

            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                
            	UsernamePasswordAuthenticationToken authentication = 
            		jwtTokenUtil.getAuthentication(
            				authToken, 
            				SecurityContextHolder.getContext().getAuthentication(),
            				userDetails
            	);
            	
                authentication.setDetails(
                	new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                //logger.info("authenticated user " + username + ", setting security context");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
		
	}

}
