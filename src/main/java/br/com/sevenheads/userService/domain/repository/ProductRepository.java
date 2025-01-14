package br.com.sevenheads.userService.domain.repository;

import br.com.sevenheads.userService.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long>  {

    Optional<Product> findById(UUID id);
}
