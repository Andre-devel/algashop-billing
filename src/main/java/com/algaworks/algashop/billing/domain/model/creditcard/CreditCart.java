package com.algaworks.algashop.billing.domain.model.creditcard;

import com.algaworks.algashop.billing.domain.model.IdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Setter(AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CreditCart {
    
    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    private OffsetDateTime createdAt;
    private UUID customerId;
    private String lastNumbers;
    private String brand;
    private Integer expMonth;
    private Integer expYear;
    
    private String gatewayCode;
    
    public static CreditCart brandNew(UUID customerId, String lastNumbers, String brand, Integer expMonth, Integer expYear, String gatewayCreditCardCode) {
        Objects.requireNonNull(customerId);
        Objects.requireNonNull(expMonth);
        Objects.requireNonNull(expYear);
        
        if (StringUtils.isAnyBlank(lastNumbers, brand, gatewayCreditCardCode)) {
            throw new IllegalArgumentException("Last numbers, brand, and gateway credit card code must be provided");
        }
        
        return new CreditCart(
                IdGenerator.generateTimeBasedUUID(),
                OffsetDateTime.now(),
                customerId,
                lastNumbers,
                brand,
                expMonth,
                expYear,
                gatewayCreditCardCode
        );
    }
    
    public void setGatewayCode(String gatewayCode) {
        if (StringUtils.isBlank(gatewayCode)) {
            throw new IllegalArgumentException("Gateway code cannot be null or empty");
        }
        
        if (this.gatewayCode != null) {
            throw new IllegalStateException("Gateway code has already been assigned");
        }
        
        this.gatewayCode = gatewayCode;
    }
}
