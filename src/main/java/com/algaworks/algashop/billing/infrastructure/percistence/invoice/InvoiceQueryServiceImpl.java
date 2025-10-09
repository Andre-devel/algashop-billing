package com.algaworks.algashop.billing.infrastructure.percistence.invoice;

import com.algaworks.algashop.billing.application.invoice.query.InvoiceOutPut;
import com.algaworks.algashop.billing.application.invoice.query.InvoiceQueryService;
import com.algaworks.algashop.billing.application.utility.Mapper;
import com.algaworks.algashop.billing.domain.model.invoice.Invoice;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceNotFoundException;
import com.algaworks.algashop.billing.domain.model.invoice.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InvoiceQueryServiceImpl implements InvoiceQueryService {
    
    private final InvoiceRepository invoiceRepository;
    private final Mapper mapper;
    
    @Override
    public InvoiceOutPut findByOrderId(String orderId) {
        Invoice invoice = invoiceRepository.findByOrderId(orderId).orElseThrow(() -> new InvoiceNotFoundException("Invoice not found for order: " + orderId));
        return mapper.convert(invoice, InvoiceOutPut.class);
    }
}
