package com.myretail.products.controller;

import com.myretail.products.constants.PriceSaveOperation;
import com.myretail.products.exception.UnsupportedOperationException;
import com.myretail.products.model.response.Product;
import com.myretail.products.model.response.ProductResponse;
import com.myretail.products.service.ProductService;
import com.myretail.products.validation.RequestValidator;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequestMapping("/products")
@RestController
public class ProductController {

    private ProductService service;
    private RequestValidator validator;

    public ProductController(ProductService service, RequestValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    @GetMapping()
    public ProductResponse get() {
        log.info("Attempt to getOne details for all products");
        throw new UnsupportedOperationException("Interacting with the full collection of products is not supported yet");
    }

    /**
     * Handles an HTTP GET request for a product given a product ID. If the product ID is valid,
     * the details of the product are retrieved up from upstream sources and provided as a response
     *
     * @param productId The id of the product to retrieve details for
     * @return The product response with product details and/or errors fetching the details
     */
    @Timed("operation.get.one")
    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable("id") Long productId) {
        log.info("Request to get details for product {}", productId);

        validator.validate(productId);

        ProductResponse response = service.getProduct(productId);
        log.trace("Responding with [{}]", response);

        return response;
    }

    /**
     * Handles an HTTP PUT request for a product given a product ID and some price details. If the
     * product ID and details are valid, then the details are saved. The response contains the
     * updated product and the appropriate code to indicate if the details were update or created.
     *
     * NOTE: Normally, a PUT doesn't return a body since the provided input is expected to be saved
     * as is to the server. Since the user may provide a description that will not be updated using
     * the RedSky endpoint (only price details are updated), the updated state of the resource is
     * returned to be extremely clear to the client about the result of the operation.
     *
     * @param productId The ID of the product resource to update
     * @param product The details to update the product with
     * @param httpResponse A handle to the HTTP Response to modify the status code appropriately
     * @return The updated product details record
     */
    @Timed("operation.put.one")
    @PutMapping("/{id}")
    public ProductResponse save(@PathVariable("id") Long productId, @RequestBody Product product, HttpServletResponse httpResponse) {
        log.info("Request to update product {} to {}", productId, product);

        validator.validate(productId);
        validator.validate(product);

        PriceSaveOperation operation = service.saveProductPrice(productId, product);
        log.trace("Saving price updates completed with a {} operation", operation);

        if (PriceSaveOperation.CREATE == operation) {
            httpResponse.setStatus(HttpServletResponse.SC_CREATED);
        }

        ProductResponse response = service.getProduct(productId);
        log.trace("Responding with [{}]", response);

        return response;
    }
}
