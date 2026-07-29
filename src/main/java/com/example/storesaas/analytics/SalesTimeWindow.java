package com.example.storesaas.analytics;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public record SalesTimeWindow(
        LocalDate referenceDate,
        ZonedDateTime currentStart,
        ZonedDateTime currentEnd,
        ZonedDateTime previousStart,
        ZonedDateTime previousEnd,
        ZonedDateTime yearAgoStart,
        ZonedDateTime yearAgoEnd) {
}
