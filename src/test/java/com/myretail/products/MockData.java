package com.myretail.products;

import com.myretail.products.model.pricing.ProductPrice;
import com.myretail.products.model.redsky.RedSkyItem;
import com.myretail.products.model.redsky.RedSkyProduct;
import com.myretail.products.model.redsky.RedSkyProductDescription;
import com.myretail.products.model.redsky.RedSkyResponse;
import com.myretail.products.model.response.Price;
import com.myretail.products.model.response.Product;
import com.myretail.products.model.response.ProductResponse;

import java.util.ArrayList;
import java.util.Arrays;

public class MockData {

    public static RedSkyResponse validRedSkyResponse() {
        RedSkyProductDescription description = new RedSkyProductDescription("Test Product");
        RedSkyItem item = new RedSkyItem("123456789", description);
        RedSkyProduct product = new RedSkyProduct(item);
        RedSkyResponse response = new RedSkyResponse(product);

        return response;
    }

    public static ProductPrice validPriceResponse() {
        ProductPrice price = new ProductPrice(123456789L, 49.99F, "USD");

        return price;
    }

    public static ProductResponse validResponse() {
        Price price = new Price(49.99F, "USD");
        Product product = new Product(123456789L, "Test Product", price);
        ProductResponse response = new ProductResponse(
                new ArrayList<>(Arrays.asList(product)),
                new ArrayList<>());

        return response;
    }

    public static Product validRequest() {
        Price price = new Price(100.00F, "USD");
        Product product = new Product(null, "Test Product", price);

        return product;
    }
}
