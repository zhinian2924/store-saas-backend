package com.example.storesaas.analytics.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalesOverviewVO(
        String generatedAt,
        String currency,
        SalesMetric today,
        SalesMetric week,
        SalesMetric month,
        List<SalesTrendPoint> trend,
        List<ProductSalesContribution> contributions) {

    // 销售指标
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SalesMetric(
            BigDecimal amount,
            BigDecimal previousAmount,
            BigDecimal previousChangeRate,
            BigDecimal yearAgoAmount,
            BigDecimal yearOverYearRate) {
    }

    // 销售趋势点
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SalesTrendPoint(
            String key,
            String label,
            BigDecimal current,
            BigDecimal previous,
            BigDecimal yearAgo) {
    }

    // 商品销售贡献
    public record ProductSalesContribution(
            Long productId,
            String productName,
            BigDecimal amount,
            BigDecimal shareRate) {
    }
}
