package com.algaworks.algashop.billing.infrastructure.creditCard.fastpay;

import com.algaworks.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import com.algaworks.algashop.billing.infrastructure.AbstractFastpayIT;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(FastpayCreditCardTokenizationAPIClientConfig.class)
class CreditCardProviderServiceFastpayImplIT extends AbstractFastpayIT {
    
    @Test
    public void shouldRegisterCreditCard() {
        LimitedCreditCard limitedCreditCard = registerCard();

        Assertions.assertThat(limitedCreditCard.getGatewayCode()).isNotBlank();
    }
    
    @Test
    public void shouldFindRegisteredCard() {
        LimitedCreditCard registeredCard = registerCard();

        LimitedCreditCard foundCard = creditCardProvider.findById(registeredCard.getGatewayCode()).orElseThrow();

        Assertions.assertThat(foundCard.getGatewayCode()).isEqualTo(registeredCard.getGatewayCode());
    }
    
    @Test
    public void shouldDeleteRegisteredCard() {
        LimitedCreditCard registeredCard = registerCard();

        creditCardProvider.delete(registeredCard.getGatewayCode());

        Assertions.assertThat(creditCardProvider.findById(registeredCard.getGatewayCode())).isEmpty();
    }
}