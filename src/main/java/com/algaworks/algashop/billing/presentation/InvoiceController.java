package com.algaworks.algashop.billing.presentation;

import com.algaworks.algashop.billing.application.invoice.management.GenerateInvoiceInput;
import com.algaworks.algashop.billing.application.invoice.management.InvoiceManagementApplicationService;
import com.algaworks.algashop.billing.application.invoice.query.InvoiceOutPut;
import com.algaworks.algashop.billing.application.invoice.query.InvoiceQueryService;
import com.algaworks.algashop.billing.domain.model.BadGatewayException;
import com.algaworks.algashop.billing.domain.model.GatewayTimeoutException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders/{orderId}/invoice")
@RequiredArgsConstructor
@Slf4j
public class InvoiceController {

    private final InvoiceQueryService invoiceQueryService;
    private final InvoiceManagementApplicationService invoiceManagementApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceOutPut generate(@PathVariable String orderId, @Valid @RequestBody GenerateInvoiceInput input) {
        input.setOrderId(orderId);

        UUID invoiceId = invoiceManagementApplicationService.generate(input);
        try {
            invoiceManagementApplicationService.processPayment(invoiceId);
        } catch (GatewayTimeoutException | BadGatewayException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error processing payment for invoice {}", invoiceId, e);
        }

        return invoiceQueryService.findByOrderId(orderId);
    }

    @GetMapping
    public InvoiceOutPut findOrder(@PathVariable String orderId) {
        return invoiceQueryService.findByOrderId(orderId);
    }
}
