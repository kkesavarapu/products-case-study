package com.myretail.products.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductError {
    public static String REDSKY = "redsky";
    public static String PRICING = "pricing";

    private String source;
    private String message;
}
