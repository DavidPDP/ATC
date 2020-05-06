//package co.edu.icesi.metrocali.atc.confs;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import co.edu.icesi.metrocali.atc.constants.ATCConstants;
//import co.edu.icesi.metrocali.atc.security.ATCAuthentication;
//import co.edu.icesi.metrocali.atc.security.ATCAuthorization;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration 
//	extends WebSecurityConfigurerAdapter {
//
//	private UserDetailsService userDetailsService;
//	
//	private PasswordEncoder passwordEncoder;
//		
//	@Autowired
//	public SecurityConfiguration(@Qualifier("operatorsService")
//		UserDetailsService userDetailsService, 
//		@Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
//		
//		this.userDetailsService = userDetailsService;
//		this.passwordEncoder = passwordEncoder;
//		
//	}
//	
//	@Override
//	@Autowired
//	public void configure(AuthenticationManagerBuilder auth) 
//			throws Exception {
//		
//		auth.userDetailsService(userDetailsService)
//			.passwordEncoder(this.passwordEncoder);
//		
//	}
//	
//	@Override
//	protected void configure(HttpSecurity httpSecurity) throws Exception {
//							
//		httpSecurity
//		.sessionManagement()
//		.sessionCreationPolicy(
//			SessionCreationPolicy.STATELESS
//		).and()
//		.cors().and()
//		.csrf().disable()
//		.authorizeRequests()
//		.antMatchers(HttpMethod.POST, 
//			ATCConstants.LOGIN_URL_PREFIX + ATCConstants.CONTROLLER_LOGIN_ULR,
//			ATCConstants.LOGIN_URL_PREFIX + ATCConstants.OMEGA_LOGIN_URL)
//		.permitAll()
//		.anyRequest().authenticated().and()
//			.addFilter(new ATCAuthentication(authenticationManager()))
//			.addFilter(new ATCAuthorization(authenticationManager()));
//		
//	}
//	
//	@Bean
//	public CorsConfigurationSource corsConfigurationSource() {
//		UrlBasedCorsConfigurationSource source = 
//				new UrlBasedCorsConfigurationSource();
//		source.registerCorsConfiguration("/**", 
//				new CorsConfiguration().applyPermitDefaultValues());
//		return source;
//	}
//	
//}
