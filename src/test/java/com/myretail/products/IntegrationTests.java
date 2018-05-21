package com.myretail.products;

import com.myretail.products.model.pricing.ProductPrice;
import com.myretail.products.model.redsky.RedSkyResponse;
import com.myretail.products.model.response.Product;
import com.myretail.products.model.response.ProductError;
import com.myretail.products.model.response.ProductResponse;
import com.myretail.products.repository.ProductPrices;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTests {

    @Autowired
    private TestRestTemplate client;

    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ProductPrices prices;

    @Test
    public void get_KnownByAllUpstream_FullResponse200() {
        /* Given */
        long productId = 123456789L;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<RedSkyResponse> response = new ResponseEntity<>(MockData.validRedSkyResponse(), HttpStatus.OK);
        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenReturn(response);

        when(prices.findById(productId)).thenReturn(Optional.of(MockData.validPriceResponse()));

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        ProductResponse actual = result.getBody();
        assertThat(actual.getErrors().size()).isEqualTo(0);
    }

    @Test
    public void get_UnknownByRedsky_404() {
        /* Given */
        long productId = 123456789L;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void get_KnownByRedskyNotFoundByPricing_PartialResponse200() {
        /* Given */
        long productId = 123456789L;

        ProductResponse expected = MockData.validResponse();
        expected.getData().get(0).setPrice(null);
        expected.getErrors().add(new ProductError(ProductError.PRICING, "Product not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        ResponseEntity<RedSkyResponse> response = new ResponseEntity<>(MockData.validRedSkyResponse(), HttpStatus.OK);
        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenReturn(response);

        when(prices.findById(productId)).thenReturn(Optional.empty());

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        ProductResponse actual = result.getBody();
        assertThat(actual).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void get_NoProductId_501() {
        /* Given */
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
    }

    @Test
    public void get_StringProductId_400() {
        /* Given */
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/notandid",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void get_OutOfRangeProductId_400() {
        /* Given */
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/-123456789",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void get_InvalidAcceptType_406() {
        /* Given */
        long productId = 123456789L;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
    }

    @Test
    public void get_RedSkyDown_500() {
        /* Given */
        long productId = 123456789L;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"));

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void put_ValidPriceUpdate_200() {
        /* Given */
        long productId = 123456789L;
        Product update = MockData.validRequest();

        ProductPrice expected = new ProductPrice(
                productId,
                update.getPrice().getValue(),
                update.getPrice().getCurrencyCode());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(update, headers);

        ResponseEntity<RedSkyResponse> response = new ResponseEntity<>(MockData.validRedSkyResponse(), HttpStatus.OK);
        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenReturn(response);

        when(prices.existsById(any())).thenReturn(true);

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.PUT,
                request,
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(prices, times(1)).save(expected);
    }

    @Test
    public void put_ValidPriceCreate_201() {
        /* Given */
        long productId = 123456789L;
        Product update = MockData.validRequest();

        ProductPrice expected = new ProductPrice(
                productId,
                update.getPrice().getValue(),
                update.getPrice().getCurrencyCode());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(update, headers);

        ResponseEntity<RedSkyResponse> response = new ResponseEntity<>(MockData.validRedSkyResponse(), HttpStatus.OK);
        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenReturn(response);

        when(prices.existsById(any())).thenReturn(false);

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.PUT,
                request,
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(prices, times(1)).save(expected);
    }

    @Test
    public void put_ValidLowerCaseCountryCodePriceUpdate_200() {
        /* Given */
        long productId = 123456789L;
        Product update = MockData.validRequest();
        update.getPrice().setCurrencyCode("uSd");

        ProductPrice expected = new ProductPrice(
                productId,
                update.getPrice().getValue(),
                "USD");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(update, headers);

        ResponseEntity<RedSkyResponse> response = new ResponseEntity<>(MockData.validRedSkyResponse(), HttpStatus.OK);
        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenReturn(response);

        when(prices.existsById(any())).thenReturn(true);

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.PUT,
                request,
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(prices, times(1)).save(expected);
    }

    @Test
    public void put_ValidPriceUpdateTooManyDecimalPlaces_200() {
        /* Given */
        long productId = 123456789L;
        Product update = MockData.validRequest();
        update.getPrice().setValue(100.3475F);

        ProductPrice expected = new ProductPrice(
                productId,
                100.35F,
                update.getPrice().getCurrencyCode());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(update, headers);

        ResponseEntity<RedSkyResponse> response = new ResponseEntity<>(MockData.validRedSkyResponse(), HttpStatus.OK);
        when(restTemplate.getForEntity(any(), eq(RedSkyResponse.class))).thenReturn(response);

        when(prices.existsById(any())).thenReturn(true);

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.PUT,
                request,
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(prices, times(1)).save(expected);
    }

    @Test
    public void put_NegativePriceUpdate_400() {
        /* Given */
        long productId = 123456789L;
        Product update = MockData.validRequest();
        update.getPrice().setValue(-100.00F);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(update, headers);

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.PUT,
                request,
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void put_InvalidCountryCodePriceUpdate_400() {
        /* Given */
        long productId = 123456789L;
        Product update = MockData.validRequest();
        update.getPrice().setCurrencyCode("AAA");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(update, headers);

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.PUT,
                request,
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void put_InvalidIdPriceUpdate_400() {
        /* Given */
        long productId = -123456789L;
        Product update = MockData.validRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Product> request = new HttpEntity<>(update, headers);

        /* When */
        ResponseEntity<ProductResponse> result = client.exchange(
                "/products/" + productId,
                HttpMethod.PUT,
                request,
                ProductResponse.class);

        /* Then */
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
