package com.algaworks.algashop.billing.application.invoice.management;

import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardRepository;
import com.algaworks.algashop.billing.domain.model.invoice.Address;
import com.algaworks.algashop.billing.domain.model.invoice.Invoice;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceNotFoundException;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceRepository;
import com.algaworks.algashop.billing.domain.model.invoice.InvoicingService;
import com.algaworks.algashop.billing.domain.model.invoice.LineItem;
import com.algaworks.algashop.billing.domain.model.invoice.Payer;
import com.algaworks.algashop.billing.domain.model.invoice.payament.Payment;
import com.algaworks.algashop.billing.domain.model.invoice.payament.PaymentGatewayService;
import com.algaworks.algashop.billing.domain.model.invoice.payament.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceManagementApplicationService {
    
    private final PaymentGatewayService paymentGatewayService;
    private final InvoicingService invoicingService;
    private final InvoiceRepository invoiceRepository;
    private final CreditCardRepository creditCardRepository;
    
    @Transactional
    public UUID generate(GenerateInvoiceInput input) {
        PaymentSettingsInput paymentSettings = input.getPaymentSettings();
        
        verifyCreditCardId(paymentSettings.getCreditCardId());
        Payer payer = convertToPayer(input.getPayer());
        
        Set<LineItem> items =  convertToLineItems(input.getItems());

        Invoice invoice = invoicingService.issue(input.getOrderId(), input.getCustomerId(), payer, items);
        invoice.changePaymentSettings(paymentSettings.getMethod(), paymentSettings.getCreditCardId());
        
        invoiceRepository.saveAndFlush(invoice);
        
        return invoice.getId();
    }
    
    @Transactional
    public void processPayment(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new InvoiceNotFoundException(String.format("Invoice with ID %s not found.", invoiceId)));
        
        PaymentRequest paymentRequest = toPaymentRequest(invoice);
        
        Payment payment;
        try {
            payment = paymentGatewayService.caputre(paymentRequest);
        } catch (Exception e) {
            String errorMessage = "Payment capture failed";
            log.error(errorMessage, e);
            invoice.cancel(errorMessage);
            invoiceRepository.save(invoice);
            return;
        }
        
        invoicingService.assignPayment(invoice, payment);
        invoiceRepository.saveAndFlush(invoice);
    }

    private PaymentRequest toPaymentRequest(Invoice invoice) {
        return PaymentRequest.builder()
                .amount(invoice.getTotalAmount())
                .method(invoice.getPaymentSettings().getMethod())
                .creditCardId(invoice.getPaymentSettings().getCreditCardId())
                .payer(invoice.getPayer())
                .invoiceId(invoice.getId())
                .build();
    }

    private Set<LineItem> convertToLineItems(Set<LineItemInput> itemsInput) {
        Set<LineItem> lineItems = new LinkedHashSet<>();
        int itemNumber = 1;
        for (LineItemInput itemInput : itemsInput) {
            lineItems.add(LineItem.builder()
                            .number(itemNumber)
                            .name(itemInput.getName())
                            .amount(itemInput.getAmount())
                    .build());
            itemNumber++;
        }
        
        return lineItems;
    }

    private Payer convertToPayer(PayerData payer) {
        AddressData addressData = payer.getAddress();
        
        return Payer.builder()
                .fullName(payer.getFullName())
                .email(payer.getEmail())
                .document(payer.getDocument())
                .phone(payer.getPhone())
                .address(Address.builder()
                        .city(addressData.getCity())
                        .state(addressData.getState())
                        .neighborhood(addressData.getNeighborhood())
                        .complement(addressData.getComplement())
                        .zipCode(addressData.getZipCode())
                        .street(addressData.getStreet())
                        .number(addressData.getNumber())
                        .build())
                .build();
    }

    private void verifyCreditCardId(UUID creditCardId) {
        if (creditCardId != null && !creditCardRepository.existsById(creditCardId)) {
            throw new CreditCardNotFoundException(String.format("Credit card with ID %s does not exist.", creditCardId));
        }
    }
}
