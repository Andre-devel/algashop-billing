package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.DomainException;

public class InvoiceNotFoundException extends DomainException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
