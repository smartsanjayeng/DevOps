package com.devops.shopping.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devops.shopping.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
