package com.example.storesaas.analytics;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.example.storesaas.analytics.vo.SalesOverviewVO;
import com.example.storesaas.common.ApiResponse;
import com.example.storesaas.common.BusinessException;
import com.example.storesaas.common.constants.ApiRoutes;
import com.example.storesaas.common.constants.Permissions;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Locale;

@RestController
@RequestMapping(ApiRoutes.STORE_ANALYTICS)
public class SalesAnalyticsController {
    private final SalesAnalyticsService service;

    public SalesAnalyticsController(SalesAnalyticsService service) {
        this.service = service;
    }

    @SaCheckPermission(Permissions.STATISTICS_VIEW)
    @GetMapping("/sales-overview")
    public ApiResponse<SalesOverviewVO> salesOverview(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        SalesPeriod salesPeriod;
        try {
            salesPeriod = SalesPeriod.valueOf(period.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BusinessException("统计周期仅支持 day、week、month");
        }
        return ApiResponse.ok(service.overview(salesPeriod, date));
    }
}
