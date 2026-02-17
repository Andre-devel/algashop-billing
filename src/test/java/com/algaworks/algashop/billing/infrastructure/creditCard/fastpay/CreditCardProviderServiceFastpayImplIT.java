package com.algaworks.algashop.billing.infrastructure.creditCard.fastpay;

import com.algaworks.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Year;
import java.util.UUID;

@SpringBootTest
@Import(FastpayCreditCardTokenizationAPIClientConfig.class)
class CreditCardProviderServiceFastpayImplIT {

    @Autowired
    private CreditCardProviderServiceFastpayImpl creditCardProvider;
    
    @Autowired
    private FastpayCreditCardTokenizationAPIClient fastpayClient;
    
    private static final UUID validCustomerId = UUID.randomUUID();
    
    private static final String alwaysPaidCardNumber = "4622943127011022";
    
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

    private LimitedCreditCard registerCard() {
        FastpayTokenizationInput input = FastpayTokenizationInput.builder()
                .number(alwaysPaidCardNumber)
                .cvv("222")
                .expMonth(12)
                .holderDocument("12345")
                .holderName("John Doe")
                .expYear(Year.now().getValue() + 5)
                
                .build();


        TokenizedCreditCardModel response = fastpayClient.tokenize(input);

        LimitedCreditCard limitedCreditCard = creditCardProvider.register(validCustomerId, response.getTokenizedCard());
        return limitedCreditCard;
    }
}