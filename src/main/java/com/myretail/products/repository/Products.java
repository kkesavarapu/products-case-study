package com.myretail.products.repository;

import com.myretail.products.configuration.RedSkyConfiguration;
import com.myretail.products.exception.RepositoryUnavailableException;
import com.myretail.products.model.redsky.RedSkyProduct;
import com.myretail.products.model.redsky.RedSkyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Repository
public class Products {
    private RedSkyConfiguration configuration;
    private RestTemplate client;

    public Products(RedSkyConfiguration configuration, RestTemplate restTemplate) {
        this.configuration = configuration;
        this.client = restTemplate;
    }

    /***
     * Retrieves a single product from the RedSky REST endpoint. The detailed JSON response from
     * RedSky is mapped down to the relevant domain model for this product.
     *
     * @param productId The identifier of the product
     * @return An optional with the product details associated with the input. If client errors
     * are encountered, they are logged and an empty product is returned
     *
     * @throws RepositoryUnavailableException If any server errors are encountered trying to
     * leverage the RedSky REST endpoint, then an exception is thrown with the appropriate status
     * code and message
     */
    public Optional<RedSkyProduct> getOne(Long productId) throws RepositoryUnavailableException {
        log.debug("Reading product {} from RedSky repository", productId);

        // Don't bother trying to make the REST call if the product ID is null
        if (null == productId) return Optional.empty();

        // Fetch product details from the RedSky REST endpoint and unwrap the envelope to return
        // the product details. Upon encountering client errors, log the issue and return an empty
        // product. Upon encountering server errors, indicate that the repository is unavailable
        ResponseEntity<RedSkyResponse> response;
        try {
            response = this.client.getForEntity(this.formatUri(productId), RedSkyResponse.class);
            return Optional.of(response.getBody().getProduct());
        } catch (HttpClientErrorException clientEx) {
            log.debug("Encountered a client exception: [HTTP {}, {}]", clientEx.getStatusCode(), clientEx.getMessage());
        } catch (HttpServerErrorException serverEx) {
            log.debug("Encountered a server exception: [HTTP {}, {}]", serverEx.getStatusCode(), serverEx.getMessage());
            throw new RepositoryUnavailableException(String.format(
                    "RedSky Unavailable; HTTP %s; Product %s",
                    serverEx.getStatusCode(),
                    productId));
        } catch (Exception ex) {
            log.debug("Encountered unexpected exception: [{}]", ex.getMessage());
        }

        return Optional.empty();
    }

    private URI formatUri(Long productId) {
        log.debug("Formatting RedSky URI for product {}", productId);

        URI uri = UriComponentsBuilder
                .fromUriString(this.configuration.getUrl())
                .path("/")
                .path(productId.toString())
                .queryParam("excludes", this.configuration.getResponseExcludes())
                .build()
                .encode()
                .toUri();

        log.trace("Formatted RedSky URI {}", uri);
        return uri;
    }
}
