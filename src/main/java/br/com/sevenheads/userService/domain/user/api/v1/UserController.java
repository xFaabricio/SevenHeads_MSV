package br.com.sevenheads.userService.domain.user.api.v1;

import java.util.Optional;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.sevenheads.userService.config.JwtService;
import br.com.sevenheads.userService.domain.entity.User;
import br.com.sevenheads.userService.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/v1/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {
	
	private final JwtService jwtService;
	
	private final UserRepository userRepository;
	
	@GetMapping("/loggedUser")
	public String loggedUser(@RequestHeader("Authorization") String authorizationHeader) {
		final String jwtToken;
		final String login;
		
		if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			return "UNAUTHORIZED";
		}
		
		jwtToken = authorizationHeader.substring(7);
		login = jwtService.extractLogin(jwtToken);
		
		Optional<User> user = userRepository.findByLogin(login);		
		ObjectMapper objectMapper = new ObjectMapper();
		
		try {
			return objectMapper.writeValueAsString(user.get());
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "ERROR";
		}	
	}
}
