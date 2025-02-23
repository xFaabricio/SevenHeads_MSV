package br.com.sevenheads.userService.domain.authentication.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
		return ResponseEntity.ok(authenticationService.register(request));
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
		return ResponseEntity.ok(authenticationService.authenticate(request));
	}

	@PostMapping("/authenticate/{idApi}")
	public ResponseEntity<AuthenticationResponse> authenticateWithIdApi(@PathVariable("idApi") UUID idApi){
		return ResponseEntity.ok(authenticationService.authenticateWithIdApi(idApi));
	}

}
