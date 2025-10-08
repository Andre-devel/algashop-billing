package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.DomainException;
import com.algaworks.algashop.billing.domain.model.IdGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Invoice {
    
    @EqualsAndHashCode.Include
    private UUID id;
    private String OrderId;
    private UUID customerId;
    
    private OffsetDateTime issuedAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime canceledAt;
    private OffsetDateTime expiresAt;
    
    private BigDecimal totalAmount;
    
    private InvoiceStatus status;
    
    private PaymentSettings paymentSettings;
    
    private Set<LineItem> items = new HashSet<>();
    
    private Payer payer;
    
    private String cancelReason;
    
    public static Invoice issue(String orderID, UUID customerId, Payer payer, Set<LineItem> items) {
        BigDecimal totalAmount = items.stream().map(LineItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new Invoice(
                IdGenerator.generateTimeBasedUUID(),
                orderID,
                customerId,
                OffsetDateTime.now(),
                null,
                null,
                OffsetDateTime.now().plusDays(3),
                totalAmount,
                InvoiceStatus.UNPAID,
                null,
                items,
                payer,
                null
        );
    }
    
    public Set<LineItem> getItems() {
        return Collections.unmodifiableSet(this.items);
    }
    
    public boolean isCanceled() {
        return InvoiceStatus.CANCELED.equals(this.status);
    }

    public boolean isUnpaid() {
        return InvoiceStatus.UNPAID.equals(this.status);
    }

    public boolean isPaid() {
        return InvoiceStatus.PAID.equals(this.status);
    }
    
    public void markAsPaid() {
        if (!isUnpaid()) {
            throw new DomainException("Invoice %s with status %s cannot be marked as paid".formatted(this.getId(), this.getStatus().toString().toLowerCase()));
        }
        
        this.setPaidAt(OffsetDateTime.now());
        this.setStatus(InvoiceStatus.PAID);
    }
    
    public void cancel(String reason) {
        if (isCanceled()) {
            throw new DomainException("Invoice %s is already canceled".formatted(this.getId()));
        }
        
        this.setCanceledAt(OffsetDateTime.now());
        this.setStatus(InvoiceStatus.CANCELED);
        this.setCancelReason(reason);
    }
    
    public void assignPaymentGatewayCode(String code) {
        if (!isUnpaid()) {
            throw new DomainException("Invoice %s with status %s cannot be edited".formatted(this.getId(), this.getStatus().toString().toLowerCase()));
        }
        
        this.getPaymentSettings().assignGatewayCode(code);
    }
    
    public void changePaymentSettings(PaymentMethod method, UUID creditCardId) {
        if (!isUnpaid()) {
            throw new DomainException("Invoice %s with status %s cannot be edited".formatted(this.getId(), this.getStatus().toString().toLowerCase()));
        }
        
        PaymentSettings paymentSettings = PaymentSettings.brandNew(method, creditCardId);
        this.setPaymentSettings(paymentSettings);
    }
}
