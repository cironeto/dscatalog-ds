package dev.cironeto.dscatalog.service;

import dev.cironeto.dscatalog.dto.ProductDto;
import dev.cironeto.dscatalog.repository.ProductRepository;
import dev.cironeto.dscatalog.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private long existingId;
    private long nonExistingId;
    private int countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;
        countTotalProducts = 25;
    }

    @Test
    void delete_DeleteResource_WhenIdExists() {
        productService.delete(existingId);
        Assertions.assertEquals((countTotalProducts - 1), productRepository.count());
    }

    @Test
    void delete_ThrowsResourceNotFoundException_WhenDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.delete(nonExistingId));
    }

    @Test
    void findAllPaged_ReturnPage() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ProductDto> result = productService.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countTotalProducts, result.getTotalElements());
    }

    @Test
    void findAllPaged_ReturnEmpty_WhenPageDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(50, 10);
        Page<ProductDto> result = productService.findAllPaged(pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void findAllPaged_ReturnSortedPage_WhenSortedByName() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
        Page<ProductDto> result = productService.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }


}
