//package co.edu.icesi.metrocali.atc.security;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.Map;
//import java.util.Map.Entry;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.LockedException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import co.edu.icesi.metrocali.atc.entities.operators.Controller;
//import co.edu.icesi.metrocali.atc.entities.policies.User;
//import co.edu.icesi.metrocali.atc.exceptions.ATCRuntimeException;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//
//public class ATCAuthentication 
//	extends UsernamePasswordAuthenticationFilter {
//
//	private AuthenticationManager authenticationManager;
//	
//	private ObjectMapper requestMapper;
//	
//	@Autowired
//	public ATCAuthentication(AuthenticationManager authenticationManager) {
//		this.authenticationManager = authenticationManager;
//		this.requestMapper = new ObjectMapper();
//	}
//	
//	@Override
//	public void doFilter(ServletRequest req, 
//			ServletResponse res, FilterChain chain)
//			throws IOException, ServletException {
//		
//		HttpServletRequest request = (HttpServletRequest) req;
//		HttpServletResponse response = (HttpServletResponse) res;
//				
//		try {
//			Authentication auth = 
//				attemptAuthentication(request, response);
//			if(auth != null) {
//				successfulAuthentication(request, response, chain, auth);
//			}else {
//				unsuccessfulAuthentication(request, response,
//					new LockedException("Forbidden"));
//			}
//		}catch(Exception e) {
//			System.out.println("ERROR");
//			e.printStackTrace();
//			unsuccessfulAuthentication(request, response,
//				new LockedException("Forbidden"));
//		}
//	}
//	
//	@Override
//	public Authentication attemptAuthentication(
//		HttpServletRequest request, HttpServletResponse response)
//		throws AuthenticationException {
//		
//		try {
//			User credentials = requestMapper
//				.readValue(request.getInputStream(), Controller.class);
//			
//			System.out.println(credentials.getUsername());
//
//			return authenticationManager.authenticate(
//				new UsernamePasswordAuthenticationToken(
//					credentials.getUsername(), 
//					credentials.getPassword(), 
//					new ArrayList<>()
//				)
//			);
//			
//		} catch (IOException e) {
//			throw new ATCRuntimeException("Invalid request. "
//				+ "The request does not contain any of the "
//				+ "fields: accountName or password.", e);
//		}
//		
//	}
//	
//	@Override
//	protected void successfulAuthentication(HttpServletRequest request, 
//			HttpServletResponse response, FilterChain chain,
//			Authentication auth) throws IOException, ServletException {
//		System.out.println("HOLA SUCCESSFUL");
//		String token = Jwts.builder().setIssuedAt(new Date()).setIssuer("")
//				.setSubject(((User)auth.getPrincipal()).getUsername())
//				.setExpiration(new Date(System.currentTimeMillis() + 86400))
//				.signWith(SignatureAlgorithm.HS512, "Hola").compact();
//		
//		response.addHeader("Authorization", "Bearer " + token);
//	}
//	
//}
