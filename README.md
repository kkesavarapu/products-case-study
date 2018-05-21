## Products Service - Proof of Concept

### Running Locally
Given this service is a case study, I prioritized making it easy to run locally (embedded MongoDB, seed data on startup, etc.). Attached to the github repository is a release with a binary (*.jar) that can be downloaded and started. Otherwise, the source code can be imported to an IDE of choice, compiled, and run from there.

#### Running Binary

**Requirements**
- Java 8
- Internet Connection (to reach RedSky)
- Compiled Binary: https://github.com/kkesavarapu/products-case-study/releases/download/v0.0.1/products.jar

```
java -jar products.jar
```

#### Loading Source Code in IDE

**Requirements**
- Java 8
- Internet Connection (to reach RedSky)
- Lombok Plugin

### Implemented Features

#### GET http://localhost:8080/products/{product_id}
- Aggregates information from RedSky endpoint and embedded MongoDB instance of pricing information
- Provides reduced response with appropriate error message if RedSky knows the product, but pricing database doesn't
- Short circuits aggregation if the product is not available in RedSky (responds with 404 w/o trying to get more data)
- Validates the product id to make sure it's valid (greater than or equal to 0)
- Metric to measure how often the endpoint gets called and how long it takes to complete the operation

#### PUT http://localhost:8080/products/{product_id}
- Validates the product id to make sure it's valid (greater than or equal to 0)
- Validates the request body to make sure it's valid
    - Price must be greater than or equal to 0
    - Price is rounded up or down to match two decimal places (i.e. 35.567 --> 35.57)
    - Currency Code is verified against valid codes (only USD, EUR, and INR setup for demonstration purposes)
    - Currency Code is transformed to all uppercase letters upon persisting
- Responds with a 200 when a price exists for a product id and was updated
- Responds with a 201 when a price does not exist for a product id and was created
- Responds with the overall updated product record to show what's changed and what hasn't clearly
- Metric to measure how often the endpoint gets called and how long it takes to complete the operation

**PUT Request Example**
```json
{
	"id": 123456789, //ignored
	"description": "New Description", //ignored
	"current_price": {
		"value": 39.99,
		"currency_code": "USD"
	}
}
```

**Example TCINs in RedSky & Pricing Mongo Instance**
- 16696652
- 15381137
- 52343337
- 51959212
- 52384081
- 50953802

**Example TCINs in RedSky Only**
- 51575286
- 16185996

### Assumptions
1. The service is a proof of concept that is meant to demonstrate the potential for a production product (i.e. not intended for customer use at this time)
2. The full json response of the upstream services is not directly presented in the response; It's mapped down to a simplified model (i.e. example response in case study documentation)
3. No user authentication and authorization is required for this proof of concept
4. The RedSky API represents the catalog of items and other sources are enrichment. If an item does not exist in RedSky, the service will respond with a 404 (not found) regardless of it exists in the pricing database.
5. A product can only have one price (i.e. multiple active prices, history of price updates, etc. are not supported)

### Some Considerations on the Journey to Production
- **Caching** of data from upstream resources to reduce the overhead of requests
- **Authorization** of update operations to identify the source of the update and to enforce who has the ability to apply updates
- **Evaluate NoSql** repository of pricing data against requirements and usage of the products endpoints. What's more important with pricing data? Consistency? Availability? etc.
- **Parallelize** retrieval of data from upstream sources as more are added to reduce the overall time spent gathering data at the expense of computing resources
- **Externalize** job to load and maintain a repository of valid ISO 4217 currency codes (if these don't change often, a flat file resource loaded into memory on startup would suffice)
- **Version** the endpoint (`products/v1` if standalone or `/v1/products` if part of a larger API) to help with change management if contract breaking modifications are required.
