package com.myretail.products.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @JsonProperty("id") Long id;
    @JsonProperty("name") String description;
    @JsonProperty("current_price")
    Price price;
}
