package com.example.storesaas.order.application;

import com.example.storesaas.catalog.api.ProductReader;
import com.example.storesaas.catalog.api.ProductSnapshot;
import com.example.storesaas.platform.error.BusinessException;
import com.example.storesaas.catalog.domain.ProductStatus;
import com.example.storesaas.order.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderPricingService {
    private final ProductReader productReader;

    public OrderPricingService(ProductReader productReader) {
        this.productReader = productReader;
    }

    public PricingResult price(Long tenantId, List<OrderLine> lines) {
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();
        for (OrderLine line : lines) {
            ProductSnapshot product = productReader.getTenantProduct(tenantId, line.productId());
            if (product.status() == null || product.status() != ProductStatus.ON_SALE) {
                throw new BusinessException(product.name() + "当前不可销售");
            }
            if (product.stock() < line.quantity()) {
                throw new BusinessException(product.name() + "库存不足");
            }
            OrderItem item = new OrderItem();
            item.setTenantId(tenantId);
            item.setProductId(product.productId());
            item.setProductName(product.name());
            item.setImageUrl(product.imageUrl());
            item.setPrice(product.price());
            item.setQuantity(line.quantity());
            item.setAmount(product.price().multiply(BigDecimal.valueOf(line.quantity())));
            items.add(item);
            total = total.add(item.getAmount());
        }
        return new PricingResult(items, total);
    }

    public record OrderLine(Long productId, Integer quantity) {
    }

    public record PricingResult(List<OrderItem> items, BigDecimal total) {
    }
}
