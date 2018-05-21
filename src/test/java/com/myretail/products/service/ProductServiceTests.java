package com.myretail.products.service;

import com.myretail.products.MockData;
import com.myretail.products.exception.ProductNotFoundException;
import com.myretail.products.model.pricing.ProductPrice;
import com.myretail.products.model.redsky.RedSkyResponse;
import com.myretail.products.model.response.ProductError;
import com.myretail.products.model.response.ProductResponse;
import com.myretail.products.repository.ProductPrices;
import com.myretail.products.repository.Products;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTests {
    @Mock
    private Products products;

    @Mock
    private ProductPrices prices;

    private ProductService service;

    @Before
    public void initialize() {
        service = new ProductService(products, prices);
    }

    @Test
    public void getProduct_ValidIdKnownByAllUpstreamSources_FullResponseObjectNoErrors() {
        /* Given */
        Long productId = 123456789L;
        ProductResponse expected = MockData.validResponse();

        RedSkyResponse redSkyResponse = MockData.validRedSkyResponse();
        ProductPrice priceResponse = MockData.validPriceResponse();

        when(products.getOne(productId)).thenReturn(Optional.of(redSkyResponse.getProduct()));
        when(prices.findById(productId)).thenReturn(Optional.of(priceResponse));

        /* When */
        ProductResponse result = service.getProduct(productId);

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void getProduct_ValidIdKnownByRedSkyUnknownByPricing_PartialResponseWithError() {
        /* Given */
        Long productId = 123456789L;
        ProductResponse expected = MockData.validResponse();
        expected.getData().get(0).setPrice(null);
        expected.getErrors().add(new ProductError(ProductError.PRICING, "Product not found"));

        RedSkyResponse redSkyResponse = MockData.validRedSkyResponse();

        when(products.getOne(productId)).thenReturn(Optional.of(redSkyResponse.getProduct()));
        when(prices.findById(productId)).thenReturn(Optional.empty());

        /* When */
        ProductResponse result = service.getProduct(productId);

        /* Then */
        assertThat(result).isNotNull();
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test(expected = ProductNotFoundException.class)
    public void getProduct_ValidIdUnkownByRedSkyKnownByPricing_ThrowsProductNotFound() {
        /* Given */
        Long productId = 123456789L;

        ProductPrice priceResponse = MockData.validPriceResponse();

        when(products.getOne(productId)).thenReturn(Optional.empty());

        /* When */
        ProductResponse result = service.getProduct(productId);

        /* Then */
        // Validation is purely that the exception is thrown
    }
}
