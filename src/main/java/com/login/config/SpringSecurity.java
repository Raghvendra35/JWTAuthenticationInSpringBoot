package com.login.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurity
{

	@Autowired
	CustomUserDetailsService userDetailsService;
	@Autowired
	JwtAuthenticationEntryPoint point;
	@Autowired
	JwtAuthenticationFilter filter;
//	@Autowired
	//PasswordEncoder passwordencoder;
	
	

//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.userDetailsService(userDetailsService).passwordEncoder(passwordencoder);
//	
//	}
	
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        var authenticationProvider = new DaoAuthenticationProvider();
//        authenticationProvider.setUserDetailsService(userDetailsService);
//        authenticationProvider.setPasswordEncoder(passwordEncoder());
//        return authenticationProvider;
//    }
	
	
	
	
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    
    
    
    
    
    
    
    
    
    
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity security) throws Exception
	{
		return security.csrf(csrf-> csrf.disable())
			  .cors(cors-> cors.disable())
			  .authorizeHttpRequests(auth-> auth.requestMatchers("/authentication","/save/data","/refresh/token").permitAll()
			  .anyRequest().authenticated())
			  .exceptionHandling(ex ->ex.authenticationEntryPoint(point))
			  .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			  .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
			  .build();
	}
	
	

	
 
	
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
    
    
    


  

}
