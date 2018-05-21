package com.myretail.products.construction;

import com.myretail.products.model.pricing.ProductPrice;
import com.myretail.products.model.redsky.RedSkyProduct;
import com.myretail.products.model.response.Price;
import com.myretail.products.model.response.Product;
import com.myretail.products.model.response.ProductError;
import com.myretail.products.model.response.ProductResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Data
public class ProductResponseBuilder {
    private RedSkyProduct product;
    private ProductPrice price;
    private List<ProductError> errors = new ArrayList<>();

    private ProductResponseBuilder(RedSkyProduct product) {
        this.product = product;

        if (null == product) {
            errors.add(new ProductError(ProductError.REDSKY, "Product not found"));
        }

        log.trace("Tracking for construction: {}", this.product);
    }

    /**
     * Entry point for the construction that relies on the RedSky product being the anchoring point
     * for the ProductResponse object. If all upstream sources were treated equal, this method
     * would change from being an entry point to a regular decorator/customizer.
     *
     * @param product The RedSky product details
     * @return A construction that's been configured with the RedSky product details
     */
    public static ProductResponseBuilder fromProduct(RedSkyProduct product) {
        log.debug("Initializing ProductResponse from RedSky Product: [{}]", product);
        return new ProductResponseBuilder(product);
    }

    /**
     * Decorator to attempt building a response with price details. If a consumer explicitly
     * calls this method with an empty input, they want to build with a price but were not able
     * to find one. Therefore, the response's error collection will contain an equivalent message
     * to reflect the reason why price is empty in the result.
     *
     * @param price Details from the pricing repository
     * @return A construction that's been configured with the price details (or error messages)
     */
    public ProductResponseBuilder withPrice(ProductPrice price) {
        log.debug("Leveraging price: {}", price);
        if (null == price) {
            log.trace("Explicitly trying to leverage an empty price; Interpreting as the price was not found");
            this.errors.add(new ProductError(ProductError.PRICING, "Product not found"));
        }

        this.price = price;
        log.trace("Tracking for construction: {}", this.price);

        return this;
    }

    /**
     * Resolves all the customization and builds a ProductResponse. Since the RedSky product is the
     * anchor for the response, an empty object will exist at minimum. All other sources will either
     * be populated and be null to reflect not finding a record.
     *
     * @return A product response with relevant details filled in and a collection of inferred errors
     */
    public ProductResponse build() {
        log.debug("Building ProductResponse with [Product: {}, Price: {}]", this.product, this.price);
        Price builtPrice = new Price();
        if (null != this.price) {
            builtPrice.setValue(this.price.getPrice());
            builtPrice.setCurrencyCode(this.price.getCurrency());
        } else {
            builtPrice = null;
        }
        log.trace("Assembled Price: [{}]", builtPrice);

        Product builtProduct = new Product();
        if (null != this.product && null != this.product.getItem() && null != this.product.getItem().getDescription()) {
            builtProduct.setId(Long.parseLong(this.product.getItem().getTcin()));
            builtProduct.setDescription(this.product.getItem().getDescription().getTitle());
            builtProduct.setPrice(builtPrice);
        }
        log.trace("Assembled Product: [{}]", builtProduct);

        ProductResponse response = new ProductResponse(Collections.singletonList(builtProduct), this.errors);
        log.trace("Assembled Response: [{}]", response);

        return response;
    }

}
