package com.example.storesaas.analytics.mapper;

import com.example.storesaas.analytics.SalesPeriod;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.storesaas.analytics.mapper.SalesAggregateRows.*;

public interface AnalyticsMapper {
    MetricAmounts selectMetricAmounts(
            @Param("tenantId") Long tenantId,
            @Param("ranges") MetricRanges ranges);

    List<AmountBucket> selectTrend(
            @Param("tenantId") Long tenantId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("period") SalesPeriod period);

    List<ProductContributionRow> selectContributions(
            @Param("tenantId") Long tenantId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("limit") int limit);
}
