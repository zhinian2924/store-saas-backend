package com.example.storesaas.analytics;

import com.example.storesaas.analytics.vo.SalesOverviewVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SalesTimeWindowFactoryTest {
    private final SalesTimeWindowFactory factory = new SalesTimeWindowFactory();

    @Test
    void metricsWindowUsesShanghaiTodayBoundaryForAnInstantFromAnotherZone() {
        ZonedDateTime now = ZonedDateTime.of(2026, 7, 28, 18, 30, 15, 0, ZoneId.of("UTC"));

        SalesTimeWindow window = factory.metricsWindow(SalesPeriod.DAY, now);

        assertEquals(LocalDate.of(2026, 7, 29), window.referenceDate());
        assertEquals(at(2026, 7, 29, 0, 0), window.currentStart());
        assertEquals(at(2026, 7, 29, 2, 30, 15), window.currentEnd());
        assertEquals(at(2026, 7, 28, 0, 0), window.previousStart());
        assertEquals(at(2026, 7, 28, 2, 30, 15), window.previousEnd());
    }

    @Test
    void metricsWindowStartsWeeksOnMonday() {
        ZonedDateTime now = at(2024, 3, 4, 14, 0);

        SalesTimeWindow window = factory.metricsWindow(SalesPeriod.WEEK, now);

        assertEquals(at(2024, 3, 4, 0, 0), window.currentStart());
        assertEquals(now, window.currentEnd());
        assertEquals(at(2024, 2, 26, 0, 0), window.previousStart());
        assertEquals(at(2024, 2, 26, 14, 0), window.previousEnd());
        assertEquals(at(2023, 2, 27, 0, 0), window.yearAgoStart());
        assertEquals(at(2023, 2, 27, 14, 0), window.yearAgoEnd());
    }

    @Test
    void metricsWindowUsesTheCurrentMonthStart() {
        ZonedDateTime now = at(2026, 6, 1, 9, 45);

        SalesTimeWindow window = factory.metricsWindow(SalesPeriod.MONTH, now);

        assertEquals(at(2026, 6, 1, 0, 0), window.currentStart());
        assertEquals(now, window.currentEnd());
        assertEquals(at(2026, 5, 1, 0, 0), window.previousStart());
        assertEquals(at(2026, 5, 1, 9, 45), window.previousEnd());
    }

    @Test
    void trendWindowUsesTheCompleteHistoricalPeriod() {
        SalesTimeWindow window = factory.trendWindow(SalesPeriod.MONTH, LocalDate.of(2025, 5, 15),
                at(2026, 7, 29, 10, 30));

        assertEquals(LocalDate.of(2025, 5, 15), window.referenceDate());
        assertEquals(at(2025, 5, 1, 0, 0), window.currentStart());
        assertEquals(at(2025, 6, 1, 0, 0), window.currentEnd());
        assertEquals(at(2025, 4, 1, 0, 0), window.previousStart());
        assertEquals(at(2025, 5, 1, 0, 0), window.previousEnd());
        assertEquals(at(2024, 5, 1, 0, 0), window.yearAgoStart());
        assertEquals(at(2024, 6, 1, 0, 0), window.yearAgoEnd());
    }

    @Test
    void metricsWindowClampsMarchThirtyFirstElapsedTimeToFebruaryEnd() {
        ZonedDateTime now = at(2026, 3, 31, 15, 20);

        SalesTimeWindow window = factory.metricsWindow(SalesPeriod.MONTH, now);

        assertEquals(at(2026, 2, 1, 0, 0), window.previousStart());
        assertEquals(at(2026, 3, 1, 0, 0), window.previousEnd());
    }

    @Test
    void metricsWindowClampsLeapDayYearAgoDate() {
        ZonedDateTime now = at(2024, 2, 29, 20, 10);

        SalesTimeWindow window = factory.metricsWindow(SalesPeriod.DAY, now);

        assertEquals(at(2023, 2, 28, 0, 0), window.yearAgoStart());
        assertEquals(at(2023, 2, 28, 20, 10), window.yearAgoEnd());
    }

    @Test
    void salesOverviewUsesTheJsonContractAndOmitsMissingValues() {
        SalesOverviewVO overview = new SalesOverviewVO(
                "2026-07-29T10:30:00+08:00",
                "CNY",
                new SalesOverviewVO.SalesMetric(new BigDecimal("100.00"), null, null, null, null),
                new SalesOverviewVO.SalesMetric(new BigDecimal("200.00"), new BigDecimal("150.00"),
                        new BigDecimal("0.3333"), null, null),
                new SalesOverviewVO.SalesMetric(new BigDecimal("300.00"), null, null,
                        new BigDecimal("250.00"), new BigDecimal("0.2")),
                List.of(new SalesOverviewVO.SalesTrendPoint("2026-07-29", "7月29日", new BigDecimal("100.00"),
                        null, null)),
                List.of(new SalesOverviewVO.ProductSalesContribution(1L, "Coffee", new BigDecimal("100.00"),
                        BigDecimal.ONE)));

        var json = new ObjectMapper().valueToTree(overview);

        assertEquals(List.of("generatedAt", "currency", "today", "week", "month", "trend", "contributions"),
                json.properties().stream().map(java.util.Map.Entry::getKey).toList());
        assertEquals(List.of("amount"), json.path("today").properties().stream()
                .map(java.util.Map.Entry::getKey).toList());
        assertEquals(List.of("key", "label", "current"), json.path("trend").get(0).properties().stream()
                .map(java.util.Map.Entry::getKey).toList());
        assertEquals(List.of("productId", "productName", "amount", "shareRate"),
                json.path("contributions").get(0).properties().stream().map(java.util.Map.Entry::getKey).toList());
    }

    private static ZonedDateTime at(int year, int month, int day, int hour, int minute) {
        return at(year, month, day, hour, minute, 0);
    }

    private static ZonedDateTime at(int year, int month, int day, int hour, int minute, int second) {
        return ZonedDateTime.of(year, month, day, hour, minute, second, 0, SalesTimeWindowFactory.ZONE);
    }
}
