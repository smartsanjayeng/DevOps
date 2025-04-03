package com.devops.shopping.serviceimpl;

import java.util.List;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.devops.shopping.entity.Product;
import com.devops.shopping.repo.ProductRepository;
import com.devops.shopping.service.ProductService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;

	@PersistenceContext
	private EntityManager entityManager;

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

	@Transactional
	@Override
	public Product createProduct(Product product) {
//	    product.setVersion(0);  // Ensure version is not null
	    Product existingProduct = productRepository.findByIdWithLock(product.getId());
	    if(existingProduct != null) {
	    	existingProduct.setName(product.getName());
	    	existingProduct.setPrice(product.getPrice());
	    	existingProduct.setDescription(product.getDescription());
	    	return productRepository.save(existingProduct);
	    }else {
	    	return productRepository.save(product);
	    }
	}

	@Transactional
	@Override
	public Product updateProduct(Long id, Product product) {
		try {
			Product existingProduct = productRepository.findById(id)
					.orElseThrow(() -> new RuntimeException("Product not found"));

			// Ensure the latest version is fetched
			entityManager.refresh(existingProduct);

			existingProduct.setName(product.getName());
			existingProduct.setDescription(product.getDescription());
			existingProduct.setPrice(product.getPrice());

			return productRepository.saveAndFlush(existingProduct); // Force immediate DB update
		} catch (ObjectOptimisticLockingFailureException e) {
			throw new RuntimeException("Product was modified by another transaction. Please refresh and try again.");
		}
	}

	@Override
	public void deleteProduct(Long id) {
		productRepository.deleteById(id);

	}

}
