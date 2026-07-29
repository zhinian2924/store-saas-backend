package com.example.storesaas.analytics;

import java.time.Duration;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

public final class SalesTimeWindowFactory {
    public static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");

    public SalesTimeWindow metricsWindow(SalesPeriod period, ZonedDateTime now) {
        ZonedDateTime shanghaiNow = requireNow(now);
        return createWindow(period, shanghaiNow.toLocalDate(), shanghaiNow);
    }

    public SalesTimeWindow trendWindow(SalesPeriod period, LocalDate date, ZonedDateTime now) {
        Objects.requireNonNull(period, "period must not be null");
        Objects.requireNonNull(date, "date must not be null");
        ZonedDateTime shanghaiNow = requireNow(now);
        ZonedDateTime currentStart = periodStart(period, date);
        ZonedDateTime currentEnd = currentStart.equals(periodStart(period, shanghaiNow.toLocalDate()))
                ? shanghaiNow
                : nextBoundary(period, currentStart);
        return createWindow(period, date, currentEnd);
    }

    private SalesTimeWindow createWindow(SalesPeriod period, LocalDate referenceDate, ZonedDateTime currentEnd) {
        Objects.requireNonNull(period, "period must not be null");
        ZonedDateTime currentStart = periodStart(period, referenceDate);
        ZonedDateTime previousStart = previousBoundary(period, currentStart);
        ZonedDateTime yearAgoStart = periodStart(period, referenceDate.minusYears(1));
        Duration elapsed = Duration.between(currentStart, currentEnd);

        return new SalesTimeWindow(
                referenceDate,
                currentStart,
                currentEnd,
                previousStart,
                boundedEnd(previousStart, elapsed, nextBoundary(period, previousStart)),
                yearAgoStart,
                boundedEnd(yearAgoStart, elapsed, nextBoundary(period, yearAgoStart)));
    }

    private ZonedDateTime requireNow(ZonedDateTime now) {
        return Objects.requireNonNull(now, "now must not be null").withZoneSameInstant(ZONE);
    }

    private ZonedDateTime periodStart(SalesPeriod period, LocalDate date) {
        LocalDate startDate = switch (period) {
            case DAY -> date;
            case WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            case MONTH -> date.withDayOfMonth(1);
        };
        return startDate.atStartOfDay(ZONE);
    }

    private ZonedDateTime previousBoundary(SalesPeriod period, ZonedDateTime currentStart) {
        return switch (period) {
            case DAY -> currentStart.minusDays(1);
            case WEEK -> currentStart.minusWeeks(1);
            case MONTH -> currentStart.minusMonths(1);
        };
    }

    private ZonedDateTime nextBoundary(SalesPeriod period, ZonedDateTime start) {
        return switch (period) {
            case DAY -> start.plusDays(1);
            case WEEK -> start.plusWeeks(1);
            case MONTH -> start.plusMonths(1);
        };
    }

    private ZonedDateTime boundedEnd(ZonedDateTime start, Duration elapsed, ZonedDateTime periodEnd) {
        ZonedDateTime elapsedEnd = start.plus(elapsed);
        return elapsedEnd.isAfter(periodEnd) ? periodEnd : elapsedEnd;
    }
}
