package com.shopmart.module.invoice.service.impl;

import com.shopmart.common.exception.ResourceNotFoundException;
import com.shopmart.module.invoice.service.InvoiceService;
import com.shopmart.module.order.entity.Order;
import com.shopmart.module.order.entity.OrderItem;
import com.shopmart.module.order.repository.OrderRepository;
import com.shopmart.util.PdfWriter;
import com.shopmart.util.PdfWriter.Line;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);

    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] generatePdf(Long userId, Long orderId) {
        Order o = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        List<Line> lines = new ArrayList<>();
        lines.add(Line.bold("SHOPMART  -  TAX INVOICE"));
        lines.add(Line.of("================================================"));
        lines.add(Line.of("Invoice for Order : " + o.getOrderNumber()));
        lines.add(Line.of("Date              : " + DATE.format(o.getCreatedAt()) + " UTC"));
        lines.add(Line.of("Order Status      : " + o.getStatus()));
        lines.add(Line.of("Payment Status    : " + o.getPaymentStatus()
                + "  (" + safe(o.getPaymentMethod()) + ")"));
        lines.add(Line.of(""));
        lines.add(Line.bold("BILL / SHIP TO"));
        lines.add(Line.of(safe(o.getShipName()) + "   " + safe(o.getShipPhone())));
        lines.add(Line.of(join(o.getShipLine1(), o.getShipLine2())));
        lines.add(Line.of(join(o.getShipCity(), o.getShipState()) + " " + safe(o.getShipPostalCode())));
        lines.add(Line.of(safe(o.getShipCountry())));
        lines.add(Line.of(""));
        lines.add(Line.bold(row("ITEM", "QTY", "UNIT", "AMOUNT")));
        lines.add(Line.of("------------------------------------------------"));
        for (OrderItem it : o.getItems()) {
            lines.add(Line.of(row(trunc(it.getProductName(), 22),
                    String.valueOf(it.getQuantity()),
                    money(it.getUnitPrice()),
                    money(it.getLineTotal()))));
        }
        lines.add(Line.of("------------------------------------------------"));
        lines.add(Line.of(totalRow("Subtotal", o.getSubtotal())));
        lines.add(Line.of(totalRow("Shipping", o.getShippingFee())));
        lines.add(Line.of(totalRow("Discount", o.getDiscount().negate())));
        lines.add(Line.bold(totalRow("TOTAL", o.getTotal())));
        lines.add(Line.of(""));
        lines.add(Line.of("Thank you for shopping with ShopMart."));
        return PdfWriter.render(lines);
    }

    private static String row(String item, String qty, String unit, String amt) {
        return String.format("%-24s %4s %10s %10s", item, qty, unit, amt);
    }

    private static String totalRow(String label, BigDecimal value) {
        return String.format("%-24s %4s %10s %10s", "", "", label, money(value));
    }

    private static String money(BigDecimal v) {
        return "INR " + (v == null ? BigDecimal.ZERO : v).toPlainString();
    }

    private static String trunc(String s, int n) {
        s = safe(s);
        return s.length() <= n ? s : s.substring(0, n - 1) + ".";
    }

    private static String join(String a, String b) {
        a = safe(a); b = safe(b);
        return b.isBlank() ? a : (a.isBlank() ? b : a + ", " + b);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
