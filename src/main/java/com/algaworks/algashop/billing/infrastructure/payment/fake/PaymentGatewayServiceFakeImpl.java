package com.algaworks.algashop.billing.infrastructure.payment.fake;

import com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod;
import com.algaworks.algashop.billing.domain.model.invoice.payament.Payment;
import com.algaworks.algashop.billing.domain.model.invoice.payament.PaymentGatewayService;
import com.algaworks.algashop.billing.domain.model.invoice.payament.PaymentRequest;
import com.algaworks.algashop.billing.domain.model.invoice.payament.PaymentStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@ConditionalOnProperty(name = "algashop.integrations.payments.provider", havingValue = "FAKE")
public class PaymentGatewayServiceFakeImpl implements PaymentGatewayService {
    
    @Override
    public Payment caputre(PaymentRequest paymentRequest) {
        return Payment.builder()
                .invoiceId(paymentRequest.getInvoiceId())
                .status(PaymentStatus.PAID)
                .method(paymentRequest.getMethod())
                .gatewayCode(UUID.randomUUID().toString())
                .build();
    }

    @Override
    public Payment findByCode(String gatewayCode) {
        return Payment.builder()
                .invoiceId(UUID.randomUUID())
                .status(PaymentStatus.PAID)
                .method(PaymentMethod.GATEWAY_BALANCE)
                .gatewayCode(UUID.randomUUID().toString())
                .build();
    }
}
