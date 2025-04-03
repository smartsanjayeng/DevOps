package com.devops.shopping.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devops.shopping.entity.Product;

public interface ProductService extends JpaRepository<Product, Long> {

}

