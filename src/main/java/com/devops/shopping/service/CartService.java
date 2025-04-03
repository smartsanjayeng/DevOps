package com.devops.shopping.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.devops.shopping.entity.CartItem;

@Service
public interface CartService extends JpaRepository<CartItem, Long> {

	void addToCart(CartItem cartItem);
    
}
