package dev.cironeto.dscatalog.service;

import dev.cironeto.dscatalog.dto.ProductDto;
import dev.cironeto.dscatalog.entity.Category;
import dev.cironeto.dscatalog.entity.Product;
import dev.cironeto.dscatalog.factory.Factory;
import dev.cironeto.dscatalog.repository.CategoryRepository;
import dev.cironeto.dscatalog.repository.ProductRepository;
import dev.cironeto.dscatalog.service.exception.DatabaseException;
import dev.cironeto.dscatalog.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;


    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDto productDto;
    private Category category;

    @BeforeEach
    public void setUp() {
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 2L;
        product = Factory.createProduct();
        category = Factory.createCategory();
        productDto = Factory.createProductDto();
        page = new PageImpl<>(List.of(product));

        Mockito.when(productRepository.getOne(existingId)).thenReturn(product);
        Mockito.when(productRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(categoryRepository.getOne(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        Mockito.doNothing().when(productRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
    }

    @Test
    void replace_ReturnProductDto_WhenIdExists(){
        ProductDto dto = productService.update(existingId, productDto);
        Assertions.assertNotNull(productDto);

        Mockito.verify(productRepository).getOne(existingId);
        Mockito.verify(productRepository).save(Factory.createProduct());
    }

    @Test
    void replace_ThrowsResourceNotFoundException_WhenIdDoesNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.update(nonExistingId, productDto));
        Mockito.verify(productRepository).getOne(nonExistingId);

    }


    @Test
    void findById_ThrowsResourceNotFoundException_WhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.findById(nonExistingId));
        Mockito.verify(productRepository).findById(nonExistingId);
    }

    @Test
    void findById_ReturnProductDto_WhenIdExists() {
        ProductDto productDto = productService.findById(existingId);
        Assertions.assertNotNull(productDto);

        Mockito.verify(productRepository).findById(existingId);
    }

    @Test
    void findAllPaged_ReturnPage() {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<ProductDto> page = productService.findAllWithCategoryFilterParam(pageable);
//
//        Assertions.assertNotNull(page);
//        Mockito.verify(productRepository).findAll(pageable);
    }

    @Test
    void delete_DoNothing_WhenIdExists() {
        Assertions.assertDoesNotThrow(() -> productService.delete(existingId));
        Mockito.verify(productRepository).deleteById(existingId);
    }

    @Test
    void delete_ThrowsResourceNotFoundException_WhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> productService.delete(nonExistingId));
        Mockito.verify(productRepository).deleteById(nonExistingId);
    }

    @Test
    void delete_ThrowsDatabaseException_WhenIdDependsOnOtherObject() {
        Assertions.assertThrows(DatabaseException.class, () -> productService.delete(dependentId));
        Mockito.verify(productRepository).deleteById(dependentId);
    }


}