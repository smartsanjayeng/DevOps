package com.devops.shopping.serviceImpl;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.devops.shopping.entity.Product;
import com.devops.shopping.repo.ProductRepository;
import com.devops.shopping.serviceimpl.ProductServiceImpl;

import jakarta.persistence.EntityManager;



@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EntityManager entityManager;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Gorkhali Radio", 5000.00, "Nepali Radio");
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(Arrays.asList(product));

        List<Product> productList = productService.getAllProducts();

        assertEquals(1, productList.size());
        assertEquals("Gorkhali Radio", productList.get(0).getName());
    }

    @Test
    void testGetProductById_Found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProductById(1L);

        assertNotNull(foundProduct);
        assertEquals("Gorkhali Radio", foundProduct.getName());
    }

    @Test
    void testGetProductById_NotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Product foundProduct = productService.getProductById(2L);

        assertNull(foundProduct);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createProduct(product);

        assertNotNull(createdProduct);
        assertEquals("Gorkhali Radio", createdProduct.getName());
    }

    @Test
    void testUpdateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProduct(1L, product);

        assertNotNull(updatedProduct);
        assertEquals("Gorkhali Radio", updatedProduct.getName());
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(2L, product);
        });

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    void testDeleteProduct_Success() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        boolean isDeleted = productService.deleteProduct(1L);

        assertTrue(isDeleted);
    }

    @Test
    void testDeleteProduct_NotFound() {
        when(productRepository.existsById(2L)).thenReturn(false);

        boolean isDeleted = productService.deleteProduct(2L);

        assertFalse(isDeleted);
    }
}
