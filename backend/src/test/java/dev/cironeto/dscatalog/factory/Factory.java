package dev.cironeto.dscatalog.factory;

import dev.cironeto.dscatalog.dto.ProductDTO;
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
        product.getCategories().add(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO(){
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }
}
