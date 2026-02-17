package com.algaworks.algashop.billing.infrastructure.payment.fastpay;

import com.algaworks.algashop.billing.domain.model.invoice.payament.Payment;
import com.algaworks.algashop.billing.domain.model.invoice.payament.PaymentGatewayService;
import com.algaworks.algashop.billing.domain.model.invoice.payament.PaymentRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "algashop.integrations.payments.provider", havingValue = "FASTPAY")
public class PaymentGatewayFastpayImpl implements PaymentGatewayService {
    
    @Override
    public Payment caputre(PaymentRequest paymentRequest) {
        return null;
    }

    @Override
    public Payment findByCode(String gatewayCode) {
        return null;
    }
}
