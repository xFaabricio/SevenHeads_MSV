package br.com.sevenheads.userService.domain.user.api.v1;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1/user")
@CrossOrigin(origins = "*")
public class UserController {

	@GetMapping("/up")	
	public String itsWorking() {		
		return "It's Up !";
	}
	
	@GetMapping("/loggedUser")	
	public String loggedUser() {		
		return "Ta logado !!";
	}
}
