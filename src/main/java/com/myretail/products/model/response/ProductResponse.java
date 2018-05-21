package com.myretail.products.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private List<Product> data = new ArrayList<>();
    private List<ProductError> errors = new ArrayList<>();
}
