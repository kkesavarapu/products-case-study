package com.myretail.products.construction;

import com.myretail.products.model.pricing.ProductPrice;
import com.myretail.products.model.response.Product;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class ProductPriceFactory {

    /**
     * Creates a ProductPrice instance from the provided product id and pricing details. The price
     * is kept to two decimal place precision (rounded up at half) and the currency code is kept to
     * upper case
     *
     * @param productId The product's identifier
     * @param product   The product's pricing details (all other attributes are ignored)
     * @return A constructed ProductPrice using the provided input
     */
    public static ProductPrice create(Long productId, Product product) {
        log.debug("Create a ProductPrice instance with product {} and price {}", productId, product);

        if (null == productId || null == product || null == product.getPrice()) {
            log.warn("Not enough information to create a product price");
            return null;
        }

        // Make sure that the price is managed to two decimal place precision
        BigDecimal priceValue = new BigDecimal(product.getPrice().getValue());
        priceValue = priceValue.setScale(2, BigDecimal.ROUND_HALF_UP);
        log.trace("Mapped price value {} to {}", product.getPrice().getValue(), priceValue.floatValue());

        // Make sure that the country code is mapped to upper case
        String currencyCode = product.getPrice().getCurrencyCode().toUpperCase();
        log.trace("Mapped currency code {} to {}", product.getPrice().getCurrencyCode(), currencyCode);

        ProductPrice price = new ProductPrice(productId, priceValue.floatValue(), currencyCode);
        log.trace("Created ProductPrice: {}", price);

        return price;
    }

}
