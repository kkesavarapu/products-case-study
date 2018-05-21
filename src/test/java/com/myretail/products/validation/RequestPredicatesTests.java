package com.myretail.products.validation;

import com.myretail.products.MockData;
import com.myretail.products.model.response.Product;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestPredicatesTests {

    @Test
    public void isValidProductId_ValidProductId_ReturnsTrue() {
        /* Given */
        Long productId = 123456789L;

        /* When */
        boolean result = RequestPredicates.isValidProductId().test(productId);

        /* Then */
        assertThat(result).isTrue();
    }

    @Test
    public void isValidProductId_ZeroProductId_ReturnsFalse() {
        /* Given */
        Long productId = 0L;

        /* When */
        boolean result = RequestPredicates.isValidProductId().test(productId);

        /* Then */
        assertThat(result).isTrue();
    }

    @Test
    public void isValidProductId_NegativeProductId_ReturnsFalse() {
        /* Given */
        Long productId = -123456789L;

        /* When */
        boolean result = RequestPredicates.isValidProductId().test(productId);

        /* Then */
        assertThat(result).isFalse();
    }

    @Test
    public void isValidProductId_NullProductId_ReturnsFalse() {
        /* Given */
        Long productId = null;

        /* When */
        boolean result = RequestPredicates.isValidProductId().test(productId);

        /* Then */
        assertThat(result).isFalse();
    }

    @Test
    public void isValidPriceValue_ValidPriceValue_ReturnsTrue(){
        /* Given */
        Product product = MockData.validRequest();

        /* When */
        boolean result = RequestPredicates.isValidPriceValue().test(product);

        /* Then */
        assertThat(result).isTrue();
    }

    @Test
    public void isValidPriceValue_ZeroPriceValue_ReturnsTrue(){
        /* Given */
        Product product = MockData.validRequest();
        product.getPrice().setValue(0F);

        /* When */
        boolean result = RequestPredicates.isValidPriceValue().test(product);

        /* Then */
        assertThat(result).isTrue();
    }

    @Test
    public void isValidPriceValue_NegativePriceValue_ReturnsFalse(){
        /* Given */
        Product product = MockData.validRequest();
        product.getPrice().setValue(-500.00F);

        /* When */
        boolean result = RequestPredicates.isValidPriceValue().test(product);

        /* Then */
        assertThat(result).isFalse();
    }

    @Test
    public void isValidPriceValue_NullProduct_ReturnsTrue(){
        /* Given */

        /* When */
        boolean result = RequestPredicates.isValidPriceValue().test(null);

        /* Then */
        assertThat(result).isFalse();
    }

    @Test
    public void isValidPriceValue_NullProductPrice_ReturnsTrue(){
        /* Given */
        Product product = MockData.validRequest();
        product.setPrice(null);

        /* When */
        boolean result = RequestPredicates.isValidPriceValue().test(product);

        /* Then */
        assertThat(result).isFalse();
    }
}
