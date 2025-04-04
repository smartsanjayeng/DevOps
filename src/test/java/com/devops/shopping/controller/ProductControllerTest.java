package com.devops.shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.devops.shopping.entity.Product;
import com.devops.shopping.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

	@MockBean
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Gorkhali Radio", 5000.00, "Nepali Radio");
    }

    @Test
    void testHelloEndpoint() throws Exception {
        mockMvc.perform(get("/api/products/test/Gorkhali"))
                .andExpect(status().isOk())
                .andExpect(content().string("hello, Gorkhali"));
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> productList = Arrays.asList(product);
        when(productService.getAllProducts()).thenReturn(productList);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Gorkhali Radio"));
    }

    @Test
    void testGetProductById_Found() throws Exception {
        when(productService.getProductById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gorkhali Radio"));
    }

    @Test
    void testGetProductById_NotFound() throws Exception {
        when(productService.getProductById(2L)).thenReturn(null);

        mockMvc.perform(get("/api/products/2"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gorkhali Radio"));
    }

    @Test
    void testUpdateProduct_Success() throws Exception {
        when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gorkhali Radio"));
    }

    @Test
    void testUpdateProduct_Conflict() throws Exception {
        when(productService.updateProduct(eq(1L), any(Product.class)))
                .thenThrow(new RuntimeException("Product was modified by another transaction"));

        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Product was modified by another transaction"));
    }

    @Test
    void testDeleteProduct_Success() throws Exception {
        when(productService.deleteProduct(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
    }

    @Test
    void testDeleteProduct_NotFound() throws Exception {
        when(productService.deleteProduct(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/products/2"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No product found with ID: 2"));
    }
}
