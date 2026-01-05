package com.hoangtrang.taskoserver.util;

import java.time.ZoneId;
import java.time.ZoneOffset;

public final class AppTime {

    private AppTime() {}

    public static final ZoneId APP_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    public static final ZoneOffset APP_OFFSET = ZoneOffset.ofHours(7);
}