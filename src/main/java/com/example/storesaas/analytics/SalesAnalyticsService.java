package com.example.storesaas.analytics;

import com.example.storesaas.analytics.mapper.AnalyticsMapper;
import com.example.storesaas.analytics.mapper.SalesAggregateRows.AmountBucket;
import com.example.storesaas.analytics.mapper.SalesAggregateRows.MetricAmounts;
import com.example.storesaas.analytics.mapper.SalesAggregateRows.MetricRange;
import com.example.storesaas.analytics.mapper.SalesAggregateRows.MetricRanges;
import com.example.storesaas.analytics.mapper.SalesAggregateRows.ProductContributionRow;
import com.example.storesaas.analytics.vo.SalesOverviewVO;
import com.example.storesaas.security.AuthContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SalesAnalyticsService {
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final int CONTRIBUTION_LIMIT = 8;
    private static final DateTimeFormatter HOUR_KEY = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:00");
    private static final DateTimeFormatter DATE_KEY = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter HOUR_LABEL = DateTimeFormatter.ofPattern("HH:00");
    private static final DateTimeFormatter DATE_LABEL = DateTimeFormatter.ofPattern("M月d日");

    private final AnalyticsMapper mapper;
    private final Clock clock;
    private final SalesTimeWindowFactory windowFactory = new SalesTimeWindowFactory();

    @Autowired
    public SalesAnalyticsService(AnalyticsMapper mapper) {
        this(mapper, Clock.system(SalesTimeWindowFactory.ZONE));
    }

    SalesAnalyticsService(AnalyticsMapper mapper, Clock clock) {
        this.mapper = Objects.requireNonNull(mapper);
        this.clock = Objects.requireNonNull(clock);
    }

    public SalesOverviewVO overview(SalesPeriod period, LocalDate date) {
        Objects.requireNonNull(period, "period must not be null");
        Long tenantId = AuthContext.tenantId();
        ZonedDateTime now = ZonedDateTime.now(clock).withZoneSameInstant(SalesTimeWindowFactory.ZONE);

        SalesTimeWindow today = windowFactory.metricsWindow(SalesPeriod.DAY, now);
        SalesTimeWindow week = windowFactory.metricsWindow(SalesPeriod.WEEK, now);
        SalesTimeWindow month = windowFactory.metricsWindow(SalesPeriod.MONTH, now);
        MetricAmounts amounts = mapper.selectMetricAmounts(tenantId, metricRanges(today, week, month));

        LocalDate referenceDate = date == null ? now.toLocalDate() : date;
        SalesTimeWindow trendWindow = windowFactory.trendWindow(period, referenceDate, now);
        List<AmountBucket> current = mapper.selectTrend(
                tenantId, local(trendWindow.currentStart()), local(trendWindow.currentEnd()), period);
        List<AmountBucket> previous = mapper.selectTrend(
                tenantId, local(trendWindow.previousStart()), local(trendWindow.previousEnd()), period);
        List<AmountBucket> yearAgo = mapper.selectTrend(
                tenantId, local(trendWindow.yearAgoStart()), local(trendWindow.yearAgoEnd()), period);
        List<ProductContributionRow> contributionRows = mapper.selectContributions(
                tenantId, local(trendWindow.currentStart()), local(trendWindow.currentEnd()), CONTRIBUTION_LIMIT);

        return new SalesOverviewVO(
                now.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                "CNY",
                metric(amounts.todayCurrent(), amounts.todayPrevious(), amounts.todayYearAgo(),
                        amounts.todayYearAgoCount()),
                metric(amounts.weekCurrent(), amounts.weekPrevious(), amounts.weekYearAgo(),
                        amounts.weekYearAgoCount()),
                metric(amounts.monthCurrent(), amounts.monthPrevious(), amounts.monthYearAgo(),
                        amounts.monthYearAgoCount()),
                trend(period, trendWindow, current, previous, yearAgo),
                contributions(contributionRows));
    }

    private MetricRanges metricRanges(
            SalesTimeWindow today,
            SalesTimeWindow week,
            SalesTimeWindow month) {
        MetricRange todayRange = MetricRange.from(today);
        MetricRange weekRange = MetricRange.from(week);
        MetricRange monthRange = MetricRange.from(month);
        LocalDateTime queryStart = List.of(
                        todayRange.yearAgoStart(), weekRange.yearAgoStart(), monthRange.yearAgoStart())
                .stream().min(LocalDateTime::compareTo).orElseThrow();
        LocalDateTime queryEnd = List.of(
                        todayRange.currentEnd(), weekRange.currentEnd(), monthRange.currentEnd())
                .stream().max(LocalDateTime::compareTo).orElseThrow();
        return new MetricRanges(todayRange, weekRange, monthRange, queryStart, queryEnd);
    }

    private SalesOverviewVO.SalesMetric metric(
            BigDecimal current,
            BigDecimal previous,
            BigDecimal yearAgo,
            Long yearAgoCount) {
        BigDecimal currentAmount = value(current);
        BigDecimal previousAmount = value(previous);
        boolean hasYearAgo = yearAgoCount != null && yearAgoCount > 0;
        BigDecimal yearAgoAmount = hasYearAgo ? value(yearAgo) : null;
        return new SalesOverviewVO.SalesMetric(
                currentAmount,
                previousAmount,
                changeRate(currentAmount, previousAmount),
                yearAgoAmount,
                hasYearAgo ? changeRate(currentAmount, yearAgoAmount) : null);
    }

    private BigDecimal changeRate(BigDecimal current, BigDecimal reference) {
        if (reference == null || reference.compareTo(ZERO) == 0) {
            return null;
        }
        return current.subtract(reference).divide(reference, 4, RoundingMode.HALF_UP);
    }

    private List<SalesOverviewVO.SalesTrendPoint> trend(
            SalesPeriod period,
            SalesTimeWindow window,
            List<AmountBucket> currentRows,
            List<AmountBucket> previousRows,
            List<AmountBucket> yearAgoRows) {
        Map<String, BigDecimal> current = bucketMap(currentRows);
        Map<String, BigDecimal> previous = bucketMap(previousRows);
        Map<String, BigDecimal> yearAgo = bucketMap(yearAgoRows);
        boolean hasYearAgo = yearAgoRows != null && !yearAgoRows.isEmpty();
        TemporalUnit unit = period == SalesPeriod.DAY ? ChronoUnit.HOURS : ChronoUnit.DAYS;
        DateTimeFormatter keyFormatter = period == SalesPeriod.DAY ? HOUR_KEY : DATE_KEY;
        DateTimeFormatter labelFormatter = period == SalesPeriod.DAY ? HOUR_LABEL : DATE_LABEL;

        List<SalesOverviewVO.SalesTrendPoint> result = new ArrayList<>();
        ZonedDateTime cursor = window.currentStart();
        long offset = 0;
        while (cursor.isBefore(window.currentEnd())) {
            ZonedDateTime previousCursor = window.previousStart().plus(offset, unit);
            ZonedDateTime yearAgoCursor = window.yearAgoStart().plus(offset, unit);
            result.add(new SalesOverviewVO.SalesTrendPoint(
                    cursor.format(keyFormatter),
                    cursor.format(labelFormatter),
                    current.getOrDefault(cursor.format(keyFormatter), ZERO),
                    bucketValue(previous, previousCursor, window.previousEnd(), keyFormatter),
                    hasYearAgo
                            ? bucketValue(yearAgo, yearAgoCursor, window.yearAgoEnd(), keyFormatter)
                            : null));
            cursor = cursor.plus(1, unit);
            offset++;
        }
        return result;
    }

    private BigDecimal bucketValue(
            Map<String, BigDecimal> values,
            ZonedDateTime cursor,
            ZonedDateTime windowEnd,
            DateTimeFormatter keyFormatter) {
        return cursor.isBefore(windowEnd)
                ? values.getOrDefault(cursor.format(keyFormatter), ZERO)
                : null;
    }

    private Map<String, BigDecimal> bucketMap(List<AmountBucket> rows) {
        Map<String, BigDecimal> values = new HashMap<>();
        if (rows != null) {
            for (AmountBucket row : rows) {
                values.put(row.bucketKey(), value(row.amount()));
            }
        }
        return values;
    }

    private List<SalesOverviewVO.ProductSalesContribution> contributions(List<ProductContributionRow> rows) {
        if (rows == null || rows.isEmpty()) {
            return List.of();
        }
        BigDecimal total = value(rows.get(0).totalAmount());
        if (total.compareTo(ZERO) == 0) {
            return List.of();
        }
        return rows.stream()
                .map(row -> new SalesOverviewVO.ProductSalesContribution(
                        row.productId(),
                        row.productName(),
                        value(row.amount()),
                        value(row.amount()).divide(total, 4, RoundingMode.HALF_UP)))
                .toList();
    }

    private BigDecimal value(BigDecimal amount) {
        return amount == null ? ZERO : amount;
    }

    private LocalDateTime local(ZonedDateTime value) {
        return value.toLocalDateTime();
    }
}
