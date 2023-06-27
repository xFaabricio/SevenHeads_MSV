package br.com.sevenheads.userService.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sevenheads.userService.domain.entity.Role;
import br.com.sevenheads.userService.domain.entity.User;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<List<Role>> findRolesByUsers(User user);
	
	Optional<Role> findRoleByKey(String key);
	
}
