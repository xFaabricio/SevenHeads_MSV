package br.com.sevenheads.userService.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sevenheads.userService.domain.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByLogin(String login);
}
