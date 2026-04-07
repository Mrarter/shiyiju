package com.shiyiju.security;

public final class CurrentUserHolder {

    private static final ThreadLocal<Long> HOLDER = new ThreadLocal<>();

    private CurrentUserHolder() {
    }

    public static void set(Long userId) {
        HOLDER.set(userId);
    }

    public static Long get() {
        return HOLDER.get();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
