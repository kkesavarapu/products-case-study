package com.myretail.products.repository;

import com.myretail.products.model.pricing.ProductPrice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductPrices extends MongoRepository<ProductPrice, Long> {
}
