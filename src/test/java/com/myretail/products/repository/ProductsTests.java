package com.myretail.products.repository;

import com.myretail.products.MockData;
import com.myretail.products.configuration.RedSkyConfiguration;
import com.myretail.products.exception.RepositoryUnavailableException;
import com.myretail.products.model.redsky.RedSkyProduct;
import com.myretail.products.model.redsky.RedSkyResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ProductsTests {
    @Mock
    private RestTemplate restTemplate;

    private Products products;

    @Before
    public void initialize() {
        RedSkyConfiguration configuration = new RedSkyConfiguration(
                "https://redsky.target.com/v2/pdp/tcin",
                "exclude1,excludes2");
        products = new Products(configuration, restTemplate);
    }

    @Test
    public void getOne_KnownId_ReturnsObject() {
        /* Given */
        Long productId = 16696652L;
        RedSkyResponse expected = MockData.validRedSkyResponse();

        ResponseEntity<RedSkyResponse> response = new ResponseEntity<>(expected, HttpStatus.OK);
        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenReturn(response);

        /* When */
        Optional<RedSkyProduct> result = products.getOne(productId);

        /* Then */
        assertThat(result.isPresent()).isTrue();

        RedSkyProduct product = result.get();
        assertThat(product.getItem()).isNotNull();
        assertThat(product.getItem().getTcin()).isEqualTo("123456789");
        assertThat(product.getItem().getDescription().getTitle()).isEqualTo("Test Product");
    }

    @Test
    public void getOne_UnknownId_ReturnsObject() {
        /* Given */
        Long productId = 16696652L;

        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "Test Client Exception"));

        /* When */
        Optional<RedSkyProduct> result = products.getOne(productId);

        /* Then */
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void getOne_NullId_ReturnsObject() {
        /* Given */
        Long productId = null;

        /* When */
        Optional<RedSkyProduct> result = products.getOne(productId);

        /* Then */
        assertThat(result.isPresent()).isFalse();
    }

    @Test(expected = RepositoryUnavailableException.class)
    public void getOne_EndpointServerError_ThrowsException() {
        /* Given */
        Long productId = 16696652L;

        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Test Server Exception"));

        /* When */
        Optional<RedSkyProduct> result = products.getOne(productId);

        /* Then */
        assertThat(result.isPresent()).isFalse();
    }
}
