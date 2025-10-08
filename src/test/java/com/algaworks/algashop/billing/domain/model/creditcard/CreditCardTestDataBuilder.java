package com.algaworks.algashop.billing.domain.model.creditcard;

import java.util.UUID;

public class CreditCardTestDataBuilder {
    
    private UUID customerId = UUID.randomUUID();
    private String lastNumbers = "1234";
    private String brand = "Visa";
    private Integer expMounth = 12;
    private Integer expYear = 2025;
    private String creditCardGatewayCode = "12345";
    
    private CreditCardTestDataBuilder() {}
    
    public static CreditCardTestDataBuilder aCreditCard() {
        return new CreditCardTestDataBuilder();
    }
    
    public CreditCard build() {
        return CreditCard.brandNew(customerId, lastNumbers, brand, expMounth, expYear, creditCardGatewayCode);
    }

    public CreditCardTestDataBuilder expYear(Integer expYear) {
        this.expYear = expYear;
        return this;
    }

    public CreditCardTestDataBuilder customerId(UUID customerId) {
        this.customerId = customerId;
        return this;
    }

    public CreditCardTestDataBuilder lastNumbers(String lastNumbers) {
        this.lastNumbers = lastNumbers;
        return this;
    }

    public CreditCardTestDataBuilder brand(String brand) {
        this.brand = brand;
        return this;
    }

    public CreditCardTestDataBuilder expMounth(Integer expMounth) {
        this.expMounth = expMounth;
        return this;
    }

    public CreditCardTestDataBuilder creditCardGatewayCode(String creditCardGatewayCode) {
        this.creditCardGatewayCode = creditCardGatewayCode;
        return this;
    }
}
