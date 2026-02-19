package com.algaworks.algashop.billing.infrastructure;

import com.algaworks.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.CreditCardProviderServiceFastpayImpl;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.FastpayCreditCardTokenizationAPIClient;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.FastpayCreditCardTokenizationAPIClientConfig;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.FastpayTokenizationInput;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.TokenizedCreditCardModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Year;
import java.util.UUID;

@Import(FastpayCreditCardTokenizationAPIClientConfig.class)
public abstract class AbstractFastpayIT {

    @Autowired
    protected FastpayCreditCardTokenizationAPIClient creditCardTokenizationAPIClient;

    @Autowired
    protected CreditCardProviderServiceFastpayImpl creditCardProvider;

    protected static final String alwaysPaidCardNumber = "4622943127011022";
    protected static final UUID validCustomerId = UUID.randomUUID();
    
    protected LimitedCreditCard registerCard() {
        FastpayTokenizationInput input = FastpayTokenizationInput.builder()
                .number(alwaysPaidCardNumber)
                .cvv("222")
                .expMonth(12)
                .holderDocument("12345")
                .holderName("John Doe")
                .expYear(Year.now().getValue() + 5)

                .build();


        TokenizedCreditCardModel response = creditCardTokenizationAPIClient.tokenize(input);

        return creditCardProvider.register(validCustomerId, response.getTokenizedCard());
    }
}
