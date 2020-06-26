package co.edu.icesi.metrocali.atc.confs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import co.edu.icesi.metrocali.atc.constants.SecurityConstants;
import co.edu.icesi.metrocali.atc.security.RestAuthenticationEntryPoint;
import co.edu.icesi.metrocali.atc.security.TokenAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration 
	extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;

    private RestAuthenticationEntryPoint unauthorizedHandler;
    
    private TokenAuthenticationFilter tokenAuthenticationFilter;
    
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    public WebSecurityConfiguration(
    		@Qualifier("operatorsService") UserDetailsService userDetailsService,
    		RestAuthenticationEntryPoint unauthorizedHandler,
    		TokenAuthenticationFilter tokenAuthenticationFilter,
    		BCryptPasswordEncoder bCryptPasswordEncoder) {
    	
    	this.userDetailsService = userDetailsService;
    	this.unauthorizedHandler = unauthorizedHandler;
    	this.tokenAuthenticationFilter = tokenAuthenticationFilter;
    	this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    	
	}
    
    @Override
	protected void configure(HttpSecurity http) throws Exception {
		
    	http.cors().and().csrf().disable()
		.authorizeRequests().antMatchers(
			SecurityConstants.Login_Url
		)
		.permitAll()
        .anyRequest().authenticated().and()
        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		
		http.addFilterBefore(tokenAuthenticationFilter, 
			UsernamePasswordAuthenticationFilter.class
		);
		
	}
    
    @Bean
    public CorsFilter corsFilter() {
    	//TODO outsource the filter
        UrlBasedCorsConfigurationSource source = 
        	new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
        
    }
    
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
        	.passwordEncoder(bCryptPasswordEncoder);
    }
  
}
