package br.com.sevenheads.userService.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sevenheads.userService.domain.entity.FormServiceHistory;

public interface FormServiceHistoryRepository extends JpaRepository<FormServiceHistory, Long> {
	
}
