package br.com.sevenheads.userService.domain.authentication.api.v1;

import br.com.sevenheads.userService.config.JwtService;
import br.com.sevenheads.userService.domain.entity.Role;
import br.com.sevenheads.userService.domain.entity.User;
import br.com.sevenheads.userService.domain.repository.RoleRepository;
import br.com.sevenheads.userService.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final RoleRepository roleRepository;
	
	private final JwtService jwtService;
	
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationResponse register(RegisterRequest request) {

		Optional<Role> role = roleRepository.findRoleByKey("ROLE_USER");
		if(role.isPresent()) {
			Role userRole = role.get();

			var user = User.builder()
					.name(request.getName())
					.login(request.getLogin())
					.password(passwordEncoder.encode(request.getPassword()))
					.roles(Arrays.asList(userRole))
					.build();

			userRepository.save(user);
			var jwtToken = jwtService.generateToken(user);
			return AuthenticationResponse.builder()
					.token(jwtToken)
					.build();
		}
		return null;
	}

	public AuthenticationResponse authenticateWithIdApi(UUID idApi) {
		var user = userRepository.findByidApi(idApi)
				.orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
		var user = userRepository.findByLogin(request.getLogin())
				.orElseThrow();
		var jwtToken = jwtService.generateToken(user);		
		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
}
