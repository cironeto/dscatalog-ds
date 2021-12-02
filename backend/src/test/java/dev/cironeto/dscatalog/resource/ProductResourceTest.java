package dev.cironeto.dscatalog.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cironeto.dscatalog.dto.ProductDto;
import dev.cironeto.dscatalog.factory.Factory;
import dev.cironeto.dscatalog.service.ProductService;
import dev.cironeto.dscatalog.service.exception.DatabaseException;
import dev.cironeto.dscatalog.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

@WebMvcTest(ProductResource.class)
class ProductResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductDto productDto;
    private PageImpl<ProductDto> page;
    private long existingId;
    private long nonExistingId;
    private long dependentId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 2L;
        productDto = Factory.createProductDto();
        page = new PageImpl<>(List.of(productDto));

        Mockito.when(productService.findAllPaged(ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productService.findById(existingId)).thenReturn(productDto);
        Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(productService.replace(ArgumentMatchers.eq(existingId), ArgumentMatchers.any()))
                .thenReturn(productDto);
        Mockito.when(productService.replace(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any()))
                .thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(productService).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        Mockito.doThrow(DatabaseException.class).when(productService).delete(dependentId);

        Mockito.when(productService.save(ArgumentMatchers.any())).thenReturn(productDto);

    }

    @Test
    void deleteShouldReturnBadRequestWhenIdDependsOnOtherObject() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/products/{id}", dependentId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNoContent());
    }


    @Test
    void findAllShouldReturnPage() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders
                        .get("/products")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void findByIdShouldReturnProductDtoWhenIdExists() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/products/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .get("/products/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void replaceShouldReturnProductDtoWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
    }

    @Test
    void replaceShouldReturnNotFoundWhenIDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .put("/products/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void saveShouldReturnProductDtoAndCreatedStatus() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
                .post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(MockMvcResultMatchers.status().isCreated());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());
    }

    //testes

}