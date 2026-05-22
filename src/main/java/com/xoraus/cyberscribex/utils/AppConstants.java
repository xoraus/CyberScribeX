package com.xoraus.cyberscribex.utils;

public final class AppConstants {

    private AppConstants() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "createdAt";
    public static final String DEFAULT_SORT_ORDER = "desc";
    public static final int MAX_PAGE_SIZE = 100;
}
