package com.algaworks.algashop.billing.domain.model.creditcard;

import com.algaworks.algashop.billing.domain.model.DomainEntityNotFoundException;

public class CreditCardNotFoundException extends DomainEntityNotFoundException {
    public CreditCardNotFoundException(String message) {
        super(message);
    }
}
