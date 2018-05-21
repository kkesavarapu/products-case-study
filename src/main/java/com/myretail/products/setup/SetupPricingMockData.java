package com.myretail.products.setup;

import com.myretail.products.model.pricing.CurrencyCode;
import com.myretail.products.model.pricing.ProductPrice;
import com.myretail.products.repository.CurrencyCodes;
import com.myretail.products.repository.ProductPrices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// Runner to load the embedded mongo database with some seed data for pricing. This is useful
// for the POC, but can be deleted when deploying this service against an independent mongo instance
// that has pricing information loaded from an external source.
@Slf4j
@Component
public class SetupPricingMockData implements ApplicationRunner {

    private ProductPrices prices;
    private CurrencyCodes currencyCodes;

    public SetupPricingMockData(ProductPrices prices, CurrencyCodes currencyCodes) {
        this.prices = prices;
        this.currencyCodes = currencyCodes;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing the embedded mongodb instance with mock data for case study");

        prices.save(new ProductPrice(16696652L, 26.00F, "USD"));
        prices.save(new ProductPrice(15381137L, 14.00F, "USD"));
        prices.save(new ProductPrice(52343337L, 69.99F, "USD"));
        prices.save(new ProductPrice(51959212L, 49.99F, "USD"));
        prices.save(new ProductPrice(52384081L, 37.99F, "USD"));
        prices.save(new ProductPrice(50953802L, 37.99F, "USD"));

        currencyCodes.save(new CurrencyCode("USD"));
        currencyCodes.save(new CurrencyCode("EUR"));
        currencyCodes.save(new CurrencyCode("INR"));

        log.debug("Initialized the embedded mongodb instance with {} product prices", prices.findAll().size());
        log.debug("Initialized the embedded mongodb instance with {} currency codes", currencyCodes.findAll().size());
    }
}
