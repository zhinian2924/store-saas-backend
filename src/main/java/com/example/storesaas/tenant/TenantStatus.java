package com.example.storesaas.tenant;

/**
 * 租户状态
 */
public final class TenantStatus {
    public static final int DISABLED = 0;// 禁用
    public static final int ACTIVE = 1;// 启用
    public static final int PENDING = 2;// 待审核
    public static final int REJECTED = 3;// 审核拒绝

    private TenantStatus() {
    }
}
