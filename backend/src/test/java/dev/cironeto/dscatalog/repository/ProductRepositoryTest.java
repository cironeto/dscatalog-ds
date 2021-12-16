package dev.cironeto.dscatalog.repository;

import dev.cironeto.dscatalog.entity.Product;
import dev.cironeto.dscatalog.factory.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    long existingId;
    long notExistingId;
    int countTotalProductInDb;

    Product product;

    @BeforeEach
    void setUp(){
        existingId = 1L;
        notExistingId = 100L;
        countTotalProductInDb = 25;
        product = Factory.createProduct();
    }


    @Test
    void delete_DeleteObject_WhenIdExists(){
        productRepository.deleteById(existingId);
        Optional<Product> result = productRepository.findById(existingId);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void delete_ThrowException_WhenIdDoesNotExists(){
        Assertions.assertThrows(EmptyResultDataAccessException.class,() -> {
            productRepository.deleteById(notExistingId);
        });
    }

    @Test
    void save_PersistWhitAutoincrement_WhenIdIsNull(){
        product.setId(null);
        product = productRepository.save(product);
        Assertions.assertNotNull(product);
        Assertions.assertEquals(countTotalProductInDb + 1, product.getId());
    }

    @Test
    void findById_ReturnNotEmptyOptional_WhenIdExists(){
        Optional<Product> optional = productRepository.findById(existingId);
        Assertions.assertFalse(optional.isEmpty());
    }

    @Test
    void findById_ReturnEmptyOptional_WhenIdNotExists(){
        Optional<Product> optional = productRepository.findById(notExistingId);
        Assertions.assertTrue(optional.isEmpty());
    }

}