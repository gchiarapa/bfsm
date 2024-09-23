package br.com.bfsm.infra.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	
	private static final Logger log = LoggerFactory.getLogger(WebSecurityConfig.class);

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		        http
//	            .authorizeHttpRequests(authorize -> authorize
//    				.requestMatchers("/cliente/**", "/movimentacoes/**")
//    				.permitAll()
//	                .anyRequest().authenticated()
//	            )
//	            .formLogin(formLogin -> formLogin
////	                .loginPage("/login")
//	                .permitAll()
//	            )
//	            .logout((logout) -> logout.permitAll())
	            .csrf(csrf -> csrf.disable())
	            .sessionManagement(sm -> 
	            sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

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
