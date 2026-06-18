package com.shopmart.module.invoice.service;

public interface InvoiceService {
    /** Renders a PDF invoice for an order owned by the given user. */
    byte[] generatePdf(Long userId, Long orderId);
}
