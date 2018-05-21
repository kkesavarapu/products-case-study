package com.myretail.products.repository;

import com.myretail.products.model.pricing.CurrencyCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyCodes extends MongoRepository<CurrencyCode, String> {
}
