package br.com.sevenheads.userService.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.sevenheads.userService.domain.repository.UserRepository;
import br.com.sevenheads.userService.domain.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	
}
