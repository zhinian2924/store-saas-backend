package com.example.storesaas.analytics.mapper;

import com.example.storesaas.analytics.SalesTimeWindow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class SalesAggregateRows {
    private SalesAggregateRows() {
    }

    public record MetricRange(
            LocalDateTime currentStart,
            LocalDateTime currentEnd,
            LocalDateTime previousStart,
            LocalDateTime previousEnd,
            LocalDateTime yearAgoStart,
            LocalDateTime yearAgoEnd) {
        public static MetricRange from(SalesTimeWindow window) {
            return new MetricRange(
                    window.currentStart().toLocalDateTime(),
                    window.currentEnd().toLocalDateTime(),
                    window.previousStart().toLocalDateTime(),
                    window.previousEnd().toLocalDateTime(),
                    window.yearAgoStart().toLocalDateTime(),
                    window.yearAgoEnd().toLocalDateTime());
        }
    }

    public record MetricRanges(
            MetricRange today,
            MetricRange week,
            MetricRange month,
            LocalDateTime queryStart,
            LocalDateTime queryEnd) {
    }

    public record MetricAmounts(
            BigDecimal todayCurrent,
            BigDecimal todayPrevious,
            BigDecimal todayYearAgo,
            Long todayYearAgoCount,
            BigDecimal weekCurrent,
            BigDecimal weekPrevious,
            BigDecimal weekYearAgo,
            Long weekYearAgoCount,
            BigDecimal monthCurrent,
            BigDecimal monthPrevious,
            BigDecimal monthYearAgo,
            Long monthYearAgoCount) {
    }

    public record AmountBucket(String bucketKey, BigDecimal amount) {
    }

    public record ProductContributionRow(
            Long productId,
            String productName,
            BigDecimal amount,
            BigDecimal totalAmount) {
    }
}
