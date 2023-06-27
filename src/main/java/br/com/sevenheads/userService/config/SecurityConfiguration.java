package br.com.sevenheads.userService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;
	
	@Autowired
	private AuthenticationProvider authenticationProvider;
	
	@SuppressWarnings("removal")
	@Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
        		.csrf()
        		.disable()
                .authorizeHttpRequests(
                		authorizeConfig -> {
                			authorizeConfig.requestMatchers("/api/v1/auth/**").permitAll();
                			authorizeConfig.requestMatchers("/server/**").permitAll();
                			authorizeConfig.requestMatchers("/logout").permitAll();
                			authorizeConfig.anyRequest().authenticated();                			                			
                		})
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
	
}
