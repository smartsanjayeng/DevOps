package com.devops.shopping.serviceimpl;

import java.util.List;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.devops.shopping.entity.Product;
import com.devops.shopping.repo.ProductRepository;
import com.devops.shopping.service.ProductService;

import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	public ProductServiceImpl(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Product getProductById(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	public Product createProduct(Product product) {
		return productRepository.save(product);
	}

	@Transactional
	@Override
	public Product updateProduct(Long id, Product product) {
		try {
			Product existingProduct = productRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Product not found"));

			existingProduct.setName(product.getName());
			existingProduct.setDescription(product.getDescription());
			existingProduct.setPrice(product.getPrice());

			return productRepository.save(existingProduct); // Force immediate DB update
		} catch (ObjectOptimisticLockingFailureException e) {
			throw new RuntimeException("Product was modified by another transaction. Please refresh and try again.");
		}
	}

	@Override
	public boolean deleteProduct(Long id) {
	    if (productRepository.existsById(id)) {
	        productRepository.deleteById(id);
	        return true; // Indicates successful deletion
	    }
	    return false; // Indicates product not found
	}

}
