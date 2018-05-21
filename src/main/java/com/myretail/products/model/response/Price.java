package com.myretail.products.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Price {
    @JsonProperty("value") float value;
    @JsonProperty("currency_code") String currencyCode;
}