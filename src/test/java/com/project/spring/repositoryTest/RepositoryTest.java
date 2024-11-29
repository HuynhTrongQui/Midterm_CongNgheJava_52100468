package com.project.spring.repositoryTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


import com.project.spring.model.Product;
import com.project.spring.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class RepositoryTest {
    @Autowired
    ProductRepository productRepository;
    @Test
    public void testAddProduct(){
        Product expectedProduct = new Product();
        expectedProduct.setName("test");
        Long id = this.productRepository.save(expectedProduct).getId();

        Product actualProduct = this.productRepository.findById(id).get();
        assertNotNull(actualProduct);
        assertEquals(actualProduct.getName(),expectedProduct.getName());
    }

}
