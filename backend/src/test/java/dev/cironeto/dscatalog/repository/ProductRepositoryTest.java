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
    public void deleteShouldDeleteObjectWhenIdExists(){
        productRepository.deleteById(existingId);
        Optional<Product> result = productRepository.findById(existingId);
        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void deleteShouldThrowExceptionWhenIdDoesNotExists(){
        Assertions.assertThrows(EmptyResultDataAccessException.class,() -> {
            productRepository.deleteById(notExistingId);
        });
    }

    @Test
    public void saveShouldPersistWhitAutoincrementWhenIdIsNull(){
        product.setId(null);
        product = productRepository.save(product);
        Assertions.assertNotNull(product);
        Assertions.assertEquals(countTotalProductInDb + 1, product.getId());
    }

    @Test
    public void findByIdShouldReturnNotEmptyOptionalWhenIdExists(){
        Optional<Product> optional = productRepository.findById(existingId);
        Assertions.assertFalse(optional.isEmpty());
    }

    @Test
    public void findByIdShouldReturnEmptyOptionalWhenIdNotExists(){
        Optional<Product> optional = productRepository.findById(notExistingId);
        Assertions.assertTrue(optional.isEmpty());
    }

}