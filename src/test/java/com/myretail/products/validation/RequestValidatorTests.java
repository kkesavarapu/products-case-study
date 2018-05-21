package com.myretail.products.validation;

import com.myretail.products.MockData;
import com.myretail.products.exception.BadRequestException;
import com.myretail.products.model.response.Product;
import com.myretail.products.repository.CurrencyCodes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestValidatorTests {

    @Mock
    private CurrencyCodes currencyCodes;

    private RequestValidator validator;

    @Before
    public void initialize() {
        validator = new RequestValidator(currencyCodes);
    }

    @Test
    public void validateProductId_Valid_DoesNothing() {
        /* Given */
        Long productId = 123456789L;

        /* When */
        validator.validate(productId);

        /* Then */
        // Verified by checking for an exception (or not if valid)
    }

    @Test(expected = BadRequestException.class)
    public void validateProductId_Negative_ThrowsBadRequestException() {
        /* Given */
        Long productId = -123456789L;

        /* When */
        validator.validate(productId);

        /* Then */
        // Verified by checking for an exception (or not if valid)
    }

    @Test(expected = BadRequestException.class)
    public void validateProductId_Null_ThrowsBadRequestException() {
        /* Given */
        Long productId = null;

        /* When */
        validator.validate(productId);

        /* Then */
        // Verified by checking for an exception (or not if valid)
    }

    @Test
    public void validateProduct_Valid_DoesNothing() {
        /* Given */
        Product product = MockData.validRequest();

        when(currencyCodes.existsById(anyString())).thenReturn(true);

        /* When */
        validator.validate(product);

        /* Then */
        // Verified by checking for an exception (or not if valid)
    }

    @Test(expected = BadRequestException.class)
    public void validateProduct_NullProduct_ThrowsBadRequestException() {
        /* Given */
        Product product = null;

        /* When */
        validator.validate(product);

        /* Then */
        // Verified by checking for an exception (or not if valid)
    }

    @Test(expected = BadRequestException.class)
    public void validateProduct_NullProductPrice_ThrowsBadRequestException() {
        /* Given */
        Product product = MockData.validRequest();
        product.setPrice(null);

        /* When */
        validator.validate(product);

        /* Then */
        // Verified by checking for an exception (or not if valid)
    }

    @Test(expected = BadRequestException.class)
    public void validateProduct_NegativePrice_ThrowsBadRequestException() {
        /* Given */
        Product product = MockData.validRequest();
        product.getPrice().setValue(-100.00F);

        /* When */
        validator.validate(product);

        /* Then */
        // Verified by checking for an exception (or not if valid)
    }

    @Test(expected = BadRequestException.class)
    public void validateProduct_UnknownCurrencyCode_ThrowsBadRequestException() {
        /* Given */
        Product product = MockData.validRequest();
        product.getPrice().setCurrencyCode("AAA");

        when(currencyCodes.existsById(anyString())).thenReturn(false);

        /* When */
        validator.validate(product);

        /* Then */
        // Verified by checking for an exception (or not if valid)
    }

    @Test(expected = BadRequestException.class)
    public void validateProduct_NullCurrencyCode_ThrowsBadRequestException() {
        /* Given */
        Product product = MockData.validRequest();
        product.getPrice().setCurrencyCode(null);

        /* When */
        validator.validate(product);

        /* Then */
        // Verified by checking for an exception (or not if valid)
    }

}
