package com.myretail.products.construction;

import com.myretail.products.MockData;
import com.myretail.products.model.pricing.ProductPrice;
import com.myretail.products.model.redsky.RedSkyProduct;
import com.myretail.products.model.response.Product;
import com.myretail.products.model.response.ProductError;
import com.myretail.products.model.response.ProductResponse;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductResponseBuilderTests {

    @Test
    public void fromProduct_ValidRedSkyProduct_ReturnBuilderWithProduct() {
        /* Given */
        ProductResponseBuilder builder;
        RedSkyProduct product = MockData.validRedSkyResponse().getProduct();

        /* When */
        builder = ProductResponseBuilder.fromProduct(product);

        /* Then */
        assertThat(builder).isNotNull();
        assertThat(builder.getProduct()).isEqualToComparingFieldByFieldRecursively(product);
    }

    @Test
    public void fromProduct_NullRedSkyProduct_ReturnBuilderWithProduct() {
        /* Given */
        ProductResponseBuilder builder;
        ProductError error = new ProductError(ProductError.REDSKY, "Product not found");

        /* When */
        builder = ProductResponseBuilder.fromProduct(null);

        /* Then */
        assertThat(builder).isNotNull();
        assertThat(builder.getProduct()).isNull();
        assertThat(builder.getErrors()).isEqualTo(Collections.singletonList(error));
    }

    @Test
    public void withPrice_ValidPrice_ReturnBuilderWithProductAndPrice() {
        /* Given */
        ProductResponseBuilder builder;
        RedSkyProduct product = MockData.validRedSkyResponse().getProduct();
        ProductPrice price = MockData.validPriceResponse();

        /* When */
        builder = ProductResponseBuilder
                .fromProduct(product)
                .withPrice(price);

        /* Then */
        assertThat(builder).isNotNull();
        assertThat(builder.getProduct()).isEqualToComparingFieldByFieldRecursively(product);
        assertThat(builder.getPrice()).isEqualToComparingFieldByFieldRecursively(price);
    }

    @Test
    public void withPrice_EmptyPrice_ReturnBuilderWithProductAndPrice() {
        /* Given */
        ProductResponseBuilder builder;
        RedSkyProduct product = MockData.validRedSkyResponse().getProduct();
        ProductError error = new ProductError(ProductError.PRICING, "Product not found");

        /* When */
        builder = ProductResponseBuilder
                .fromProduct(product)
                .withPrice(null);

        /* Then */
        assertThat(builder).isNotNull();
        assertThat(builder.getProduct()).isEqualToComparingFieldByFieldRecursively(product);
        assertThat(builder.getPrice()).isNull();
        assertThat(builder.getErrors()).isEqualTo(Collections.singletonList(error));
    }

    @Test
    public void build_ValidRedSkyProductValidPrice_ReturnFullProductResponse() {
        /* Given */
        RedSkyProduct product = MockData.validRedSkyResponse().getProduct();
        ProductPrice price = MockData.validPriceResponse();
        ProductResponse expected = MockData.validResponse();

        /* When */
        ProductResponse result = ProductResponseBuilder
                .fromProduct(product)
                .withPrice(price)
                .build();

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void build_ValidRedSkyProductEmptyPrice_ReturnPartialProductResponseWithError() {
        /* Given */
        RedSkyProduct product = MockData.validRedSkyResponse().getProduct();

        ProductResponse expected = MockData.validResponse();
        expected.getData().get(0).setPrice(null);

        /* When */
        ProductResponse result = ProductResponseBuilder
                .fromProduct(product)
                .withPrice(null)
                .build();

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result.getData().get(0)).isEqualToComparingFieldByFieldRecursively(expected.getData().get(0));
        assertThat(result.getErrors().size()).isEqualTo(1);
    }

    @Test
    public void build_NullRedSkyProductValidPrice_ReturnEmptyResponse() {
        /* Given */
        ProductPrice price = MockData.validPriceResponse();
        ProductResponse expected = MockData.validResponse();
        expected.getData().set(0, new Product());

        /* When */
        ProductResponse result = ProductResponseBuilder
                .fromProduct(null)
                .withPrice(price)
                .build();

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result.getData().get(0)).isEqualToComparingFieldByFieldRecursively(expected.getData().get(0));
        assertThat(result.getErrors().size()).isEqualTo(1);
    }

    @Test
    public void build_NullRedSkyProductEmptyPrice_ReturnEmptyResponse() {
        /* Given */
        ProductResponse expected = MockData.validResponse();
        expected.getData().set(0, new Product());

        /* When */
        ProductResponse result = ProductResponseBuilder
                .fromProduct(null)
                .withPrice(null)
                .build();

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result.getData().get(0)).isEqualToComparingFieldByFieldRecursively(expected.getData().get(0));
        assertThat(result.getErrors().size()).isEqualTo(2);
    }
}
