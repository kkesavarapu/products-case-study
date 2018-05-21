package com.myretail.products.model.pricing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "prices")
public class ProductPrice {
    @Id
    @Field("id")
    private Long productId;

    @Field("price")
    private float price;

    @Field("currency")
    private String currency;
}
