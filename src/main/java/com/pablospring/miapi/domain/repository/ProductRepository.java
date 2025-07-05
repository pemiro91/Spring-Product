package com.pablospring.miapi.domain.repository;

import com.pablospring.miapi.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUserId(Long usuarioId);
}
