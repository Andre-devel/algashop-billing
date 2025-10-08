package com.algaworks.algashop.billing.domain.model.invoice.payament;

public interface PaymentGatewayService {
    Payment caputre(PaymentRequest paymentRequest);
    Payment findByCode(String gatewayCode);
}
