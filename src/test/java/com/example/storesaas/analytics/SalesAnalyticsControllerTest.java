package com.example.storesaas.analytics;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.analytics.vo.SalesOverviewVO;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.ApiRoutes;
import com.example.storesaas.common.constants.Permissions;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SalesAnalyticsControllerTest {
    @Test
    void controllerDeclaresExpectedRoutePermissionAndDefaultPeriod() throws Exception {
        RequestMapping classMapping = SalesAnalyticsController.class.getAnnotation(RequestMapping.class);
        Method method = SalesAnalyticsController.class.getMethod(
                "salesOverview", String.class, LocalDate.class);
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        SaCheckPermission permission = method.getAnnotation(SaCheckPermission.class);
        RequestParam period = (RequestParam) method.getParameterAnnotations()[0][0];

        assertArrayEquals(new String[]{ApiRoutes.STORE_ANALYTICS}, classMapping.value());
        assertArrayEquals(new String[]{"/sales-overview"}, getMapping.value());
        assertArrayEquals(new String[]{Permissions.STATISTICS_VIEW}, permission.value());
        assertEquals("month", period.defaultValue());
    }

    @Test
    void controllerForwardsParsedPeriodAndDate() {
        SalesAnalyticsService service = mock(SalesAnalyticsService.class);
        SalesOverviewVO overview = emptyOverview();
        LocalDate date = LocalDate.of(2026, 7, 1);
        when(service.overview(SalesPeriod.WEEK, date)).thenReturn(overview);

        var response = new SalesAnalyticsController(service).salesOverview("week", date);

        assertSame(overview, response.data());
        verify(service).overview(SalesPeriod.WEEK, date);
    }

    @Test
    void controllerRejectsUnsupportedPeriodWithChineseMessage() {
        SalesAnalyticsService service = mock(SalesAnalyticsService.class);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> new SalesAnalyticsController(service).salesOverview("quarter", null));

        assertEquals("统计周期仅支持 day、week、month", exception.getMessage());
        verifyNoInteractions(service);
    }

    @Test
    void controllerDoesNotMaskServiceFailuresAsPeriodErrors() {
        SalesAnalyticsService service = mock(SalesAnalyticsService.class);
        IllegalStateException failure = new IllegalStateException("database unavailable");
        when(service.overview(SalesPeriod.MONTH, null)).thenThrow(failure);

        IllegalStateException actual = assertThrows(
                IllegalStateException.class,
                () -> new SalesAnalyticsController(service).salesOverview("month", null));

        assertSame(failure, actual);
    }

    private SalesOverviewVO emptyOverview() {
        SalesOverviewVO.SalesMetric metric = new SalesOverviewVO.SalesMetric(
                java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, null, null, null);
        return new SalesOverviewVO(
                "2026-07-29T10:30:00+08:00",
                "CNY",
                metric,
                metric,
                metric,
                List.of(),
                List.of());
    }
}
