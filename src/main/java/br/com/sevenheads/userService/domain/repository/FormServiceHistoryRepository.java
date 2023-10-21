package br.com.sevenheads.userService.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.sevenheads.userService.domain.entity.FormServiceHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FormServiceHistoryRepository extends JpaRepository<FormServiceHistory, Long> {

    Optional<List<FormServiceHistory>> findFormServiceHistoriesByUuidFormServiceOrderByCreateDateDesc(UUID uuid);

    Optional<FormServiceHistory> findFirstByUuidFormService(UUID uuid);
}
