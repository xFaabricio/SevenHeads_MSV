package br.com.sevenheads.userService.authentication.api.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
	
	private String name;
	
	private String login;
	
	private String password;

	private String email;	
	
}
