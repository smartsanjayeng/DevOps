package com.devops.shopping.service;

import java.util.List;

import com.devops.shopping.entity.Product;

public interface ProductService {
	List<Product> getAllProducts();

	Product getProductById(Long id);

	Product createProduct(Product product);

	Product updateProduct(Long id, Product product);

	boolean deleteProduct(Long id);

}
