package com.algaworks.algashop.billing.domain.model.invoice;

import com.algaworks.algashop.billing.domain.model.IdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PaymentSettings {
    
    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private UUID creditCardId;
    private String gatewayCode;
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    
    @OneToOne(mappedBy = "paymentSettings")
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PACKAGE)
    private Invoice invoice;

    static PaymentSettings brandNew(PaymentMethod method, UUID creditCardId) {
        Objects.requireNonNull(method);
        
        if (method == PaymentMethod.CREDIT_CARD) {
            Objects.requireNonNull(creditCardId, "Credit card ID must be provided for credit card payments");
        }
        
        return new PaymentSettings(
                IdGenerator.generateTimeBasedUUID(),
                creditCardId,
                null,
                method,
                null
        );
    }
    
    void assignGatewayCode(String gatewayCode) {
        if (StringUtils.isBlank(gatewayCode)) {
            throw new IllegalArgumentException("Gateway code cannot be null or empty");
        }
        
        if (this.gatewayCode != null) {
            throw new IllegalStateException("Gateway code has already been assigned");
        }
        
        setGatewayCode(gatewayCode);
    }
}
