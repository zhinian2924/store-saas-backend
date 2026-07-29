package com.example.storesaas.analytics;

import com.example.storesaas.analytics.mapper.AnalyticsMapper;
import com.example.storesaas.analytics.mapper.SalesAggregateRows.AmountBucket;
import com.example.storesaas.analytics.mapper.SalesAggregateRows.MetricAmounts;
import com.example.storesaas.analytics.mapper.SalesAggregateRows.MetricRanges;
import com.example.storesaas.analytics.mapper.SalesAggregateRows.ProductContributionRow;
import com.example.storesaas.analytics.vo.SalesOverviewVO;
import com.example.storesaas.security.AuthContext;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SalesAnalyticsServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-07-29T02:30:00Z"),
            ZoneId.of("Asia/Shanghai"));

    @Test
    void overviewUsesFiveTenantQueriesAndBuildsMoneyFirstContract() {
        AnalyticsMapper mapper = mock(AnalyticsMapper.class);
        when(mapper.selectMetricAmounts(eq(42L), any())).thenReturn(new MetricAmounts(
                decimal("100"), decimal("80"), decimal("70"), 1L,
                decimal("500"), ZERO, ZERO, 0L,
                decimal("1000"), decimal("800"), ZERO, 1L));
        when(mapper.selectTrend(eq(42L), any(), any(), eq(SalesPeriod.DAY)))
                .thenReturn(
                        List.of(new AmountBucket("2026-07-29 00:00", decimal("10"))),
                        List.of(new AmountBucket("2026-07-28 00:00", decimal("8"))),
                        List.of());
        when(mapper.selectContributions(eq(42L), any(), any(), eq(8))).thenReturn(List.of(
                new ProductContributionRow(1L, "招牌咖啡", decimal("60"), decimal("200")),
                new ProductContributionRow(2L, "拿铁", decimal("40"), decimal("200"))));

        SalesOverviewVO result;
        try (MockedStatic<AuthContext> auth = mockStatic(AuthContext.class)) {
            auth.when(AuthContext::tenantId).thenReturn(42L);
            result = new SalesAnalyticsService(mapper, CLOCK).overview(SalesPeriod.DAY, null);
        }

        assertEquals("CNY", result.currency());
        assertEquals(decimal("100"), result.today().amount());
        assertEquals(decimal("0.2500"), result.today().previousChangeRate());
        assertEquals(decimal("70"), result.today().yearAgoAmount());
        assertNull(result.week().previousChangeRate());
        assertNull(result.week().yearAgoAmount());
        assertNull(result.month().yearOverYearRate());
        assertEquals(11, result.trend().size());
        assertEquals(decimal("10"), result.trend().get(0).current());
        assertEquals(decimal("8"), result.trend().get(0).previous());
        assertNull(result.trend().get(0).yearAgo());
        assertEquals(ZERO, result.trend().get(1).current());
        assertEquals(decimal("0.3000"), result.contributions().get(0).shareRate());
        assertEquals(decimal("0.2000"), result.contributions().get(1).shareRate());

        ArgumentCaptor<MetricRanges> ranges = ArgumentCaptor.forClass(MetricRanges.class);
        verify(mapper).selectMetricAmounts(eq(42L), ranges.capture());
        verify(mapper, times(3)).selectTrend(eq(42L), any(), any(), eq(SalesPeriod.DAY));
        verify(mapper).selectContributions(eq(42L), any(), any(), eq(8));
        assertEquals(LocalDate.of(2026, 7, 29), ranges.getValue().today().currentStart().toLocalDate());
        assertTrue(ranges.getValue().queryStart().isBefore(ranges.getValue().queryEnd()));
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void overviewReturnsEmptyContributionsWhenSalesTotalIsZero() {
        AnalyticsMapper mapper = mock(AnalyticsMapper.class);
        when(mapper.selectMetricAmounts(anyLong(), any())).thenReturn(new MetricAmounts(
                ZERO, ZERO, ZERO, 0L,
                ZERO, ZERO, ZERO, 0L,
                ZERO, ZERO, ZERO, 0L));
        when(mapper.selectTrend(anyLong(), any(), any(), any())).thenReturn(List.of());
        when(mapper.selectContributions(anyLong(), any(), any(), anyInt())).thenReturn(List.of(
                new ProductContributionRow(1L, "零元商品", ZERO, ZERO)));

        SalesOverviewVO result;
        try (MockedStatic<AuthContext> auth = mockStatic(AuthContext.class)) {
            auth.when(AuthContext::tenantId).thenReturn(7L);
            result = new SalesAnalyticsService(mapper, CLOCK)
                    .overview(SalesPeriod.MONTH, LocalDate.of(2025, 5, 20));
        }

        assertTrue(result.contributions().isEmpty());
        assertEquals(31, result.trend().size());
        assertNull(result.trend().get(30).previous());
        assertTrue(result.trend().stream().allMatch(point -> point.yearAgo() == null));
    }

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static BigDecimal decimal(String value) {
        return new BigDecimal(value);
    }
}
