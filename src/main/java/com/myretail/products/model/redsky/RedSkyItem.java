package com.myretail.products.model.redsky;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedSkyItem {
    @JsonProperty("tcin") private String tcin;
    @JsonProperty("product_description") private RedSkyProductDescription description;

}
