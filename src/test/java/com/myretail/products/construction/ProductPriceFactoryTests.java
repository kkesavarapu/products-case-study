package com.myretail.products.construction;

import com.myretail.products.MockData;
import com.myretail.products.model.pricing.ProductPrice;
import com.myretail.products.model.response.Product;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductPriceFactoryTests {

    @Test
    public void create_ValidIdValidPrice_MatchingProductPrice() {
        /* Given */
        Long productId = 123456789L;
        Product product = MockData.validRequest();

        /* When */
        ProductPrice result = ProductPriceFactory.create(productId, product);

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getPrice()).isEqualTo(product.getPrice().getValue());
        assertThat(result.getCurrency()).isEqualTo(product.getPrice().getCurrencyCode());
    }

    @Test
    public void create_ValidIdValidPriceTooManyDecimalPlacesAboveHalf_MatchingProductPriceTwoDecimalPlacesRoundedUp() {
        /* Given */
        Long productId = 123456789L;
        Product product = MockData.validRequest();
        product.getPrice().setValue(100.567F);

        /* When */
        ProductPrice result = ProductPriceFactory.create(productId, product);

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getPrice()).isEqualTo(100.57F);
        assertThat(result.getCurrency()).isEqualTo(product.getPrice().getCurrencyCode());
    }

    @Test
    public void create_ValidIdValidPriceTooManyDecimalPlacesBelowHalf_MatchingProductPriceTwoDecimalPlacesRoundedDown() {
        /* Given */
        Long productId = 123456789L;
        Product product = MockData.validRequest();
        product.getPrice().setValue(100.343F);

        /* When */
        ProductPrice result = ProductPriceFactory.create(productId, product);

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getPrice()).isEqualTo(100.34F);
        assertThat(result.getCurrency()).isEqualTo(product.getPrice().getCurrencyCode());
    }

    @Test
    public void create_ValidIdValidPriceLowerCaseCurrency_MatchingProductPriceUpperCaseCurrency() {
        /* Given */
        Long productId = 123456789L;
        Product product = MockData.validRequest();
        product.getPrice().setCurrencyCode("uSd");

        /* When */
        ProductPrice result = ProductPriceFactory.create(productId, product);

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getPrice()).isEqualTo(product.getPrice().getValue());
        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    public void create_ValidIdNullProduct_NullProductPrice() {
        /* Given */
        Long productId = 123456789L;
        Product product = null;

        /* When */
        ProductPrice result = ProductPriceFactory.create(productId, product);

        /* Then */
        assertThat(result).isNull();
    }

    @Test
    public void create_ValidIdNullPrice_NullProductPrice() {
        /* Given */
        Long productId = 123456789L;
        Product product = MockData.validRequest();
        product.setPrice(null);

        /* When */
        ProductPrice result = ProductPriceFactory.create(productId, product);

        /* Then */
        assertThat(result).isNull();
    }

    @Test
    public void create_NullIdValidPrice_NullProductPrice() {
        /* Given */
        Long productId = null;
        Product product = MockData.validRequest();

        /* When */
        ProductPrice result = ProductPriceFactory.create(productId, product);

        /* Then */
        assertThat(result).isNull();
    }

    @Test
    public void create_NullIdNullProduct_NullProductPrice() {
        /* Given */
        Long productId = null;
        Product product = null;

        /* When */
        ProductPrice result = ProductPriceFactory.create(productId, product);

        /* Then */
        assertThat(result).isNull();
    }
}
