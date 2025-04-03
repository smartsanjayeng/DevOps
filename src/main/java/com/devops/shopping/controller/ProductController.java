package com.devops.shopping.controller;

import com.devops.shopping.entity.Product;
import com.devops.shopping.service.ProductService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts() {
		return ResponseEntity.ok(productService.getAllProducts());
	}

	@GetMapping("/{id}")
	public ResponseEntity<String> getProductById(@PathVariable("id") Long id) {
		Product product = productService.getProductById(id);
		if (product == null) {
//	        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
	        return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
	    }
//	    return new ResponseEntity<>(product, HttpStatus.OK);
	    return new ResponseEntity<>(product.toString(), HttpStatus.OK); // Return Product's string representation if needed

	}

	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		return ResponseEntity.ok(productService.createProduct(product));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable("id") Long id, @RequestBody Product product) {
		try {
			Product updatedProduct = productService.updateProduct(id, product);
			return ResponseEntity.ok(updatedProduct);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable("id") Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
}
