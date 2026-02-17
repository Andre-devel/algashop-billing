package com.algaworks.algashop.billing.infrastructure.creditCard.fastpay;

import com.algaworks.algashop.billing.infrastructure.payment.AlgaShopPaymentProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class FastpayCreditCardTokenizationAPIClientConfig {
    
    @Bean
    public FastpayCreditCardTokenizationAPIClient FastpayCreditCardTokenizationAPIClient(
            RestClient.Builder builder,
            AlgaShopPaymentProperties properties,
            @Value("${algashop.integrations.payments.fastpay.public-token}") String publicToken) {
        AlgaShopPaymentProperties.FastpayProperties fastpayProperties = properties.getFastpay();

        RestClient restClient = builder.baseUrl(fastpayProperties.getHostname()).requestInterceptor((req, body, execution) -> {
            req.getHeaders().add("Token", publicToken);
            return execution.execute(req, body);
        }).build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory proxyFactory = HttpServiceProxyFactory.builderFor(adapter).build();
        
        return proxyFactory.createClient(FastpayCreditCardTokenizationAPIClient.class);
    }
}
