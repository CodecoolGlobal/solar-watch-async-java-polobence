package com.codecool.solarwatch.model.user;

public enum Permission {
    CITY_READ("city:read"),
    CITY_CREATE("city:create"),
    CITY_UPDATE("city:update"),
    CITY_DELETE("city:delete"),

    SUNTIMES_READ("suntimes:read"),
    SUNTIMES_CREATE("suntimes:create"),
    SUNTIMES_UPDATE("suntimes:update"),
    SUNTIMES_DELETE("suntimes:delete");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
