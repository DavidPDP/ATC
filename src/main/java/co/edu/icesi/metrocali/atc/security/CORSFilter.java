package co.edu.icesi.metrocali.atc.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

/**
 * Allows defining the protocol and policies of Cross-origin resource sharing (CORS).
 * which will be validated for each request made to the server. To customize the 
 * policies, it's recommended to review the good practices.
 * https://livebook.manning.com/book/cors-in-action/chapter-6/
 */
@Component
public class CORSFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse Httpresponse = (HttpServletResponse) response;
		Httpresponse.setHeader("Access-Control-Allow-Origin", "*");
		Httpresponse.setHeader("Access-Control-Allow-Credentials", "true");
		Httpresponse.setHeader("Access-Control-Allow-Methods", 
				"GET, POST, PATCH, DELETE");
		Httpresponse.setHeader("Access-Control-Max-Age", "3600");
		Httpresponse.setHeader("Access-Control-Allow-Headers", 
				"X-Requested-With, Content-Type, Authorization, Origin, Accept, "
				+ "Access-Control-Request-Method, Access-Control-Request-Headers");
		
		chain.doFilter(request, response);
		
	}
	
}
