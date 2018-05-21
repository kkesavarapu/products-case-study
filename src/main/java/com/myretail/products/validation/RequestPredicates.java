package com.myretail.products.validation;

import com.myretail.products.model.response.Product;

import java.util.function.Predicate;

public class RequestPredicates {

    public static Predicate<Long> isValidProductId() {
        return p -> (null != p && p >= 0);
    }

    public static Predicate<Product> isValidPriceValue() {
        return p -> (null != p && null!= p.getPrice() && p.getPrice().getValue() >= 0);
    }

}
