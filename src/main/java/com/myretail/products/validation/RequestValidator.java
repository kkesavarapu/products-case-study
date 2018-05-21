package com.myretail.products.validation;

import com.myretail.products.exception.BadRequestException;
import com.myretail.products.model.response.Product;
import com.myretail.products.repository.CurrencyCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RequestValidator {
    private CurrencyCodes currencyCodes;

    public RequestValidator(CurrencyCodes currencyCodes) {
        this.currencyCodes = currencyCodes;
    }

    public void validate(Long productId) {
        log.debug("Validating product id {}", productId);
        if (!RequestPredicates.isValidProductId().test(productId)) {
            log.trace("Product ID {} is not valid");
            throw new BadRequestException("A valid product id greater than or equal to 0 must be provided");
        }
    }

    public void validate(Product product) {
        log.debug("Validating product request {}", product);

        if (null == product || null == product.getPrice()) {
            log.trace("Product {} is not valid; Missing information", product);
            throw new BadRequestException("Not enough information provided to update price");
        }

        if(!RequestPredicates.isValidPriceValue().test(product)) {
            log.trace("Product {} is not valid; Price out of range", product);
            throw new BadRequestException("Price must be greater than or equal to 0");
        }

        if(null == product.getPrice().getCurrencyCode() ||
                !currencyCodes.existsById(product.getPrice().getCurrencyCode().toUpperCase())) {
            log.trace("Product {} is not valid; Unknown currency code", product);
            throw new BadRequestException("Price currency code is unknown");
        }
    }
}
