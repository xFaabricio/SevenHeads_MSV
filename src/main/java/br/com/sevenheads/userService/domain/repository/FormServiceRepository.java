package br.com.sevenheads.userService.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sevenheads.userService.domain.entity.FormService;

public interface FormServiceRepository extends JpaRepository<FormService, Long> {
	
	Optional<FormService> findById(UUID id);
	
}
