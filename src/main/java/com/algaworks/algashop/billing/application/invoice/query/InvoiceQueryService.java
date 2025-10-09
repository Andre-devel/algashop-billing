package com.algaworks.algashop.billing.application.invoice.query;

public interface InvoiceQueryService {
    InvoiceOutPut findByOrderId(String orderId);
}
