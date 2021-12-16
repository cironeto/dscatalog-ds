package dev.cironeto.dscatalog.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.cironeto.dscatalog.dto.ProductDto;
import dev.cironeto.dscatalog.factory.Factory;
import dev.cironeto.dscatalog.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;


    private long existingId;
    private long nonExistingId;
    private int countTotalProducts;
    private String operatorUsername;
    private String operatorPassword;
    private String adminUsername;
    private String adminPassword;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 100L;
        countTotalProducts = 25;
        operatorUsername = "alex@gmail.com";
        operatorPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";
    }

    @Test
    void findAll_ShouldReturnSortedPage_WhenSortedByName() throws Exception {
        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders
                        .get("/products?page=0&size=10&sort=name,asc")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(countTotalProducts));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content").exists());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("PC Gamer"));
    }

    @Test
    void update_ReturnProductDto_WhenIdExistsAndUserLogged() throws Exception {
        String accessToken = tokenUtil.obtainAccessToken(mockMvc, operatorUsername, operatorPassword);

        ProductDto productDto = Factory.createProductDto();
        String jsonBody = objectMapper.writeValueAsString(productDto);

        String expectedName = productDto.getName();
        String expectedDescription = productDto.getDescription();

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders
                        .put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + accessToken)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existingId));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedName));
        result.andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedDescription));
    }

    @Test
    void update_ReturnNotFound_WhenIdDoesNotExist() throws Exception {
        ProductDto productDto = Factory.createProductDto();
        String jsonBody = objectMapper.writeValueAsString(productDto);

        ResultActions result =
                mockMvc.perform(MockMvcRequestBuilders
                        .put("/products/{id}", nonExistingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNotFound());

    }
}
