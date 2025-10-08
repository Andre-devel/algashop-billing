package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.DomainException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

class InvoiceTest {

    @Test
    public void shouldCreateInvoiceSuccessfully() {
        String orderID = "01226N0693HDA";
        UUID customerId = UUID.randomUUID();
        Payer payer = InvoiceTestDataBuilder.aPayer();
        Set<LineItem> singleton = Collections.singleton(InvoiceTestDataBuilder.aLineItem());
        
        Invoice invoice = Invoice.issue(orderID, customerId, payer,singleton);
        
        Assertions.assertThat(invoice).isNotNull();
        Assertions.assertThat(invoice.getId()).isNotNull();
        Assertions.assertThat(invoice.getIssuedAt()).isNotNull();
        Assertions.assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.UNPAID);
        Assertions.assertThat(invoice.getItems().stream().findFirst()).isEqualTo(singleton.stream().findFirst());
        Assertions.assertThat(invoice.getTotalAmount()).isEqualTo(InvoiceTestDataBuilder.aLineItem().getAmount());
        Assertions.assertThat(invoice.getCustomerId()).isEqualTo(customerId);
        Assertions.assertThat(invoice.getPayer()).isEqualTo(payer);
        Assertions.assertThat(invoice.getOrderId()).isEqualTo(orderID);
        Assertions.assertThat(invoice.getExpiresAt()).isNotNull();
        
    }
    
    @Test
    public void shouldMarkAsPaidSuccessfully() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice().build();
        
        invoice.markAsPaid();
        
        Assertions.assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.PAID);
        Assertions.assertThat(invoice.getPaidAt()).isNotNull();
    }
    
    @Test
    public void shouldCancelSuccessfully() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice().build();
        
        String reason = "Customer requested cancellation";
        invoice.cancel(reason);
        
        Assertions.assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.CANCELED);
        Assertions.assertThat(invoice.getCanceledAt()).isNotNull();
        Assertions.assertThat(invoice.getCancelReason()).isEqualTo(reason);
    }
    
    @Test
    public void shouldChengePaymentSettingsSuccessfully() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice().build();
        
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        UUID creditCardId = UUID.randomUUID();
        
        invoice.changePaymentSettings(paymentMethod, creditCardId);
        
        Assertions.assertThat(invoice.getPaymentSettings().getPaymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(invoice.getPaymentSettings().getCreditCardId()).isEqualTo(creditCardId);
    }
    
    @Test
    public void shouldAssignGatewayCodeSuccessfully() {
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        UUID creditCardId = UUID.randomUUID();
        
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .paymentSettings(paymentMethod, creditCardId)
                .build();
        
        String gatewayCode = "PAY-123456";
        invoice.assignPaymentGatewayCode(gatewayCode);
        
        Assertions.assertThat(invoice.getPaymentSettings().getGatewayCode()).isEqualTo(gatewayCode);
    }
    
    @Test
    public void shouldNotCreateInvoiceWhenItemsIsEmpty() {
        String orderID = "01226N0693HDA";
        UUID customerId = UUID.randomUUID();
        Payer payer = InvoiceTestDataBuilder.aPayer();
        Set<LineItem> items = Collections.emptySet();
        
        Assertions.assertThatThrownBy(() -> Invoice.issue(orderID, customerId, payer, items))
                .isInstanceOf(IllegalArgumentException.class);
        
    }
    
    @Test
    public void shouldNotMarkAsPaidWhenInvoiceIsCanceled() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .status(InvoiceStatus.CANCELED)
                .build();
        
        Assertions.assertThatThrownBy(invoice::markAsPaid)
                .isInstanceOf(DomainException.class);
    }
    
    @Test
    public void shouldNotCancelWhenInvoiceIsAlreadyCanceled() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .status(InvoiceStatus.CANCELED)
                .build();
        
        Assertions.assertThatThrownBy(() -> invoice.cancel("Another reason"))
                .isInstanceOf(DomainException.class);
    }
    
    @Test
    public void shouldNotChangePaymentSettingsWhenInvoiceIsPaid() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .status(InvoiceStatus.PAID)
                .build();
        
        Assertions.assertThatThrownBy(() -> invoice.changePaymentSettings(PaymentMethod.CREDIT_CARD, UUID.randomUUID()))
                .isInstanceOf(DomainException.class);
    }
    
    @Test
    public void shouldNotAssignGatewayCodeWhenInvoiceIsPaid() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice()
                .status(InvoiceStatus.PAID)
                .paymentSettings(PaymentMethod.CREDIT_CARD, UUID.randomUUID())
                .build();
        
        Assertions.assertThatThrownBy(() -> invoice.assignPaymentGatewayCode("PAY-654321"))
                .isInstanceOf(DomainException.class);
    }
    
    @Test
    public void shouldNotChangeItemsCollection() {
        Invoice invoice = InvoiceTestDataBuilder.anInvoice().build();
        Collection<LineItem> items = invoice.getItems();
        
        Assertions.assertThatThrownBy(() -> items.add(InvoiceTestDataBuilder.aLineItem()))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}