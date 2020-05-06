//package co.edu.icesi.metrocali.atc.security;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//import co.edu.icesi.metrocali.atc.constants.ATCConstants;
//import co.edu.icesi.metrocali.atc.exceptions.BadRequestException;
//import io.jsonwebtoken.Jwts;
//
//public class ATCAuthorization extends BasicAuthenticationFilter{
//
//	public ATCAuthorization(AuthenticationManager authenticationManager) {
//		super(authenticationManager);
//	}
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest req, 
//			HttpServletResponse res, FilterChain chain)
//			throws IOException, ServletException {
//		System.out.println("HOLA HEADER FILTER");
//		String header = req.getHeader(
//				ATCConstants.HEADER_AUTHORIZACION_KEY);
//		
//		if (header != null && header.startsWith(
//				ATCConstants.TOKEN_BEARER_PREFIX)) {
//			
//			UsernamePasswordAuthenticationToken authentication = 
//				getAuthentication(req);
//			SecurityContextHolder.getContext()
//				.setAuthentication(authentication);
//			
//		}
//		
//		chain.doFilter(req, res);
//	
//	}
//	
//	private UsernamePasswordAuthenticationToken 
//		getAuthentication(HttpServletRequest request) {
//		
//		String token = request.getHeader(
//				ATCConstants.HEADER_AUTHORIZACION_KEY);
//		
//		if(token != null) {
//			
//			String accountName = Jwts.parser()
//				.setSigningKey("1234")
//				.parseClaimsJws(
//					token.replace(ATCConstants.TOKEN_BEARER_PREFIX, "")
//				).getBody()
//				.getSubject();
//			
//			System.out.println(accountName + "HOLA FILTER, kk");
//			
//			if(accountName != null) {
//				return new UsernamePasswordAuthenticationToken(
//					accountName, null, new ArrayList<>()
//				);
//			}else {
//				throw new BadRequestException("The request doesn't "
//					+ "contain the user field.");
//			}
//			
//		}else {
//			throw new BadRequestException("The request doesn't "
//				+ "contain the Authorization header.");
//		}
//	}
//	
//	
//}
