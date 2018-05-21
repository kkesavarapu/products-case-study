package com.myretail.products.service;

import com.myretail.products.constants.PriceSaveOperation;
import com.myretail.products.construction.ProductPriceFactory;
import com.myretail.products.construction.ProductResponseBuilder;
import com.myretail.products.exception.ProductNotFoundException;
import com.myretail.products.model.pricing.ProductPrice;
import com.myretail.products.model.redsky.RedSkyProduct;
import com.myretail.products.model.response.Product;
import com.myretail.products.model.response.ProductResponse;
import com.myretail.products.repository.ProductPrices;
import com.myretail.products.repository.Products;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProductService {
    private Products products;
    private ProductPrices prices;

    public ProductService(Products products, ProductPrices prices) {
        this.products = products;
        this.prices = prices;
    }

    /**
     * Orchestrates fetching details from the various repositories/sources for a product. The
     * RedSky repository is treated as the source of truth for if a product exists in the catalog.
     *
     * @param productId The product's identifier used to find it in all repositories
     * @return A ProductResponse object with the relevant details and potentially errors populated
     */
    public ProductResponse getProduct(Long productId) {
        log.debug("Orchestrating retrieval of product {} from upstream sources", productId);

        // Fetch product details from RedSky. Since it's our source of truth for the product catalog,
        // indicate that a product isn't found if a match wasn't acquired
        log.debug("Retrieving product details from RedSky");
        Optional<RedSkyProduct> product = products.getOne(productId);
        if (!product.isPresent()) {
            log.error("Product {} not found on RedSky", productId);
            throw new ProductNotFoundException("No products found in catalog with id " + productId);
        }
        log.trace("RedSky Product: [{}]", product);

        // Fetch pricing details for the product. Not finding a record for the item is ok.
        log.debug("Retrieving price details from repository");
        Optional<ProductPrice> price = prices.findById(productId);
        log.trace("Pricing: [{}]", price);

        // Assemble the response given data from all the upstream sources to complete this operation
        ProductResponse response = ProductResponseBuilder
                .fromProduct(product.get())
                .withPrice(price.orElse(null))
                .build();
        log.trace("Response: [{}]", response);

        return response;
    }

    /**
     * Orchestrates saving pricing details of a product. Any other attributes provided (i.e. description)
     * are ignored for the purposes of this operation. Only price value and currency code are saved
     *
     * @param productId The id to update with the new price
     * @param product   The price value and currency details
     * @return Indication of if the save operation resulted in an update or an insert
     */
    public PriceSaveOperation saveProductPrice(Long productId, Product product) {
        log.debug("Orchestrating save of product price for product {}: {}", productId, product);
        PriceSaveOperation operation = PriceSaveOperation.UPDATE;

        if (!prices.existsById(productId)) {
            log.info("Product {} not found in repository; Update operation to create", productId);
            operation = PriceSaveOperation.CREATE;
        }

        ProductPrice price = ProductPriceFactory.create(productId, product);
        prices.save(price);
        log.trace("Product price saved: [{}]", price);

        return operation;
    }
}
