package com.example.storesaas.user;

import com.example.storesaas.common.constants.Permissions;

import java.util.Arrays;
import java.util.List;

public final class StaffPermissions {
    private static final List<String> OWNER = List.of(
            Permissions.STORE_VIEW, Permissions.STORE_UPDATE,
            Permissions.PRODUCT_VIEW, Permissions.PRODUCT_ADD, Permissions.PRODUCT_UPDATE,
            Permissions.INVENTORY_VIEW, Permissions.INVENTORY_ADJUST,
            Permissions.ORDER_VIEW,
            Permissions.STAFF_VIEW, Permissions.STAFF_ADD, Permissions.STAFF_UPDATE, Permissions.STAFF_DISABLE,
            Permissions.STATISTICS_VIEW
    );

    private static final List<String> CASHIER = List.of(Permissions.PRODUCT_VIEW, Permissions.ORDER_VIEW);

    private static final List<String> WAREHOUSE = List.of(
            Permissions.PRODUCT_VIEW,
            Permissions.INVENTORY_VIEW, Permissions.INVENTORY_ADJUST
    );

    private static final List<String> MANAGER_ASSISTANT = List.of(
            Permissions.PRODUCT_VIEW, Permissions.PRODUCT_ADD, Permissions.PRODUCT_UPDATE,
            Permissions.INVENTORY_VIEW, Permissions.INVENTORY_ADJUST,
            Permissions.ORDER_VIEW, Permissions.STATISTICS_VIEW
    );

    private static final List<String> GRANTABLE = List.of(
            Permissions.STORE_VIEW,
            Permissions.PRODUCT_VIEW, Permissions.PRODUCT_ADD, Permissions.PRODUCT_UPDATE,
            Permissions.INVENTORY_VIEW, Permissions.INVENTORY_ADJUST,
            Permissions.ORDER_VIEW,
            Permissions.STATISTICS_VIEW
    );

    private StaffPermissions() {
    }

    public static List<String> owner() {
        return OWNER;
    }

    public static List<String> defaults(String role) {
        StaffRole staffRole = parseRole(role);
        return switch (staffRole) {
            case OWNER -> OWNER;
            case CASHIER -> CASHIER;
            case WAREHOUSE -> WAREHOUSE;
            case MANAGER_ASSISTANT -> MANAGER_ASSISTANT;
        };
    }

    public static List<String> parse(String permissions, String role) {
        if (permissions == null || permissions.isBlank()) {
            return defaults(role);
        }
        return Arrays.stream(permissions.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .filter(OWNER::contains)
                .distinct()
                .toList();
    }

    public static String join(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "";
        }
        return String.join(",", permissions.stream()
                .filter(OWNER::contains)
                .distinct()
                .toList());
    }

    public static String joinGrantable(List<String> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return "";
        }
        return String.join(",", permissions.stream()
                .filter(GRANTABLE::contains)
                .distinct()
                .toList());
    }

    public static List<String> grantable() {
        return GRANTABLE;
    }

    public static StaffRole parseRole(String role) {
        if (role == null || role.isBlank()) {
            return StaffRole.OWNER;
        }
        try {
            return StaffRole.valueOf(role);
        } catch (IllegalArgumentException ignored) {
            return StaffRole.CASHIER;
        }
    }

    public static boolean isOwner(String role) {
        return parseRole(role) == StaffRole.OWNER;
    }
}
