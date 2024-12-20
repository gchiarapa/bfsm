package br.com.bfsm.infra.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
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
public class WebSecurityConfig {
	
	private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);
	
	@Autowired
	SecurityFilter securityFilter;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		        http
	            .csrf(csrf -> csrf.disable())
	            .sessionManagement(sm -> 
	            {
					try {
						sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
						.and()
						.authorizeHttpRequests(authorize -> authorize
								.requestMatchers(HttpMethod.POST,"/login").permitAll()
								.requestMatchers("/swagger-ui/**", "swagger-ui**", "/v3/api-docs/**", "/v3/api-docs**").permitAll()
								.anyRequest().authenticated()
								.and()
								.addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class));
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

		return http.build();
	}
	
	@Bean
	public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder passEncoder() {
		return new BCryptPasswordEncoder();
	}

}
