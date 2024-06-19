package br.com.sevenheads.userService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;
	
	@Autowired
	private AuthenticationProvider authenticationProvider;

	public String crossOriginAllowedHeaders="header1,header2, *" ;
	public String crossOriginAllowedSites="site1,site2, * ";

	@SuppressWarnings("removal")
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.cors()
				.and()
				.csrf()
				.disable()
				.authorizeHttpRequests(authorizeConfig -> {
					authorizeConfig.requestMatchers("/swagger-ui.html").permitAll();
					authorizeConfig.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll();
					authorizeConfig.requestMatchers("/v1/api/auth/**").permitAll();
					authorizeConfig.requestMatchers("/v1/formService/**").permitAll();
					authorizeConfig.requestMatchers("/server/**").permitAll();
					authorizeConfig.requestMatchers("/logout").permitAll();
					authorizeConfig.anyRequest().authenticated();
				})
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.headers()
				.frameOptions()
				.sameOrigin().addHeaderWriter((request,response)->{
					response.setHeader("Cache-Control","no-cache, no-store, max-age=0, must-revalidate, private");
					response.setHeader("Pragma","no-cache");
					response.setHeader("Access-Control-Allow-Origin",this.crossOriginAllowedSites);
				})
				.and()
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(false);
		configuration.setAllowedHeaders(Arrays.asList(crossOriginAllowedHeaders.split(",")));
		configuration.setAllowedOrigins(Arrays.asList(crossOriginAllowedSites.split(",")));
		configuration.addAllowedMethod(HttpMethod.OPTIONS);
		configuration.addAllowedMethod(HttpMethod.GET);
		configuration.addAllowedMethod(HttpMethod.POST);
		configuration.addAllowedMethod(HttpMethod.PUT);
		configuration.addAllowedMethod(HttpMethod.DELETE);
		configuration.addExposedHeader("Authorization");
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

}
