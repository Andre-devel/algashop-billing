package com.algaworks.algashop.billing.infrastructure.percistence.creditcard;

import com.algaworks.algashop.billing.application.creditcard.query.CreditCardOutput;
import com.algaworks.algashop.billing.application.creditcard.query.CreditCardQueryService;
import com.algaworks.algashop.billing.application.utility.Mapper;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardNotFoundException;
import com.algaworks.algashop.billing.domain.model.creditcard.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditCardQueryServiceImpl implements CreditCardQueryService {
    
    private final CreditCardRepository creditCardRepository;
    private final Mapper mapper;
    
    @Override
    public CreditCardOutput findOne(UUID customerId, UUID creditCardId) {
        return creditCardRepository.findByCustomerIdAndId(customerId, creditCardId)
                .map(creditCard -> mapper.convert(creditCard, CreditCardOutput.class))
                .orElseThrow(() -> new CreditCardNotFoundException("Credit card not found for id: " + creditCardId));
    }

    @Override
    public List<CreditCardOutput> findByCustomerId(UUID customerId) {
        return creditCardRepository.findAllByCustomerId(customerId).stream()
                .map(creditCard -> mapper.convert(creditCard, CreditCardOutput.class))
                .toList();
    }
}
