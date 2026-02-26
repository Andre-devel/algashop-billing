package com.algaworks.algashop.billing.infrastructure;

import com.algaworks.algashop.billing.domain.model.creditcard.LimitedCreditCard;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.CreditCardProviderServiceFastpayImpl;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.FastpayCreditCardTokenizationAPIClient;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.FastpayCreditCardTokenizationAPIClientConfig;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.FastpayTokenizationInput;
import com.algaworks.algashop.billing.infrastructure.creditCard.fastpay.TokenizedCreditCardModel;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.extension.responsetemplating.TemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.Year;
import java.util.Collections;
import java.util.UUID;

@Import(FastpayCreditCardTokenizationAPIClientConfig.class)
public abstract class AbstractFastpayIT {

    @Autowired
    protected FastpayCreditCardTokenizationAPIClient creditCardTokenizationAPIClient;

    @Autowired
    protected CreditCardProviderServiceFastpayImpl creditCardProvider;

    protected static final String alwaysPaidCardNumber = "4622943127011022";
    protected static final UUID validCustomerId = UUID.randomUUID();
    
    protected static WireMockServer wireMockFastpay;
    
    public static void startWireMock() {
        wireMockFastpay = new WireMockServer(options()
                .port(8788)
                .usingFilesUnderDirectory("src/test/resources/wiremock/fastpay")
                .extensions(new ResponseTemplateTransformer(
                        TemplateEngine.defaultTemplateEngine(),
                        true,
                        new ClasspathFileSource("src/test/resources/wiremock/fastpay"),
                        Collections.emptyList()
                )));
        
        wireMockFastpay.start();
    }
    
    public static void stopWireMock() {
        wireMockFastpay.stop();
    }
    
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
