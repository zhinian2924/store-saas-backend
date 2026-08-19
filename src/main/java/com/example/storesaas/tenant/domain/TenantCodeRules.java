package com.example.storesaas.tenant.domain;

public final class TenantCodeRules {
    public static final int RETRY_LIMIT = 10;
    public static final int MAX_PREFIX_LENGTH = 24;
    public static final String DEFAULT_PREFIX = "store";

    private TenantCodeRules() {
    }
}
