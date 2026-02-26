package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.DomainEntityNotFoundException;

public class InvoiceNotFoundException extends DomainEntityNotFoundException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
