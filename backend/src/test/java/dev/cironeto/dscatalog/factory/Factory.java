package dev.cironeto.dscatalog.factory;

import dev.cironeto.dscatalog.dto.ProductDto;
import dev.cironeto.dscatalog.entity.Category;
import dev.cironeto.dscatalog.entity.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct(){
        Product product = new Product(
                1L,
                "iPhone",
                "iPhone 128Gb",
                3000.0,
                "http://img.com",
                Instant.now());
        product.getCategories().add(createCategory());
        return product;
    }

    public static ProductDto createProductDto(){
        Product product = createProduct();
        return new ProductDto(product, product.getCategories());
    }

    public static Category createCategory(){
        return new Category(2L, "Electronics");

    }
}
