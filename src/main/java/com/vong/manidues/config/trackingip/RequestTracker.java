package com.vong.manidues.config.trackingip;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

public class RequestTracker {

    private static final ConcurrentHashMap<String, RequestInfo> requestMap =
            new ConcurrentHashMap<>();

    public static String getRequestMap() {
        return requestMap.toString();
    }

    public static void trackRequest(HttpServletRequest request) {
        String requestIp = request.getRemoteAddr();
        String requestUserAgent = request.getHeader("User-Agent");
        int requestCount = requestMap.getOrDefault(
                requestIp, new RequestInfo()).getRequestCount() + 1;

        requestMap.put(requestIp, new RequestInfo(
                Instant.now(),
                requestCount,
                requestUserAgent
        ));
    }

    public static int getRequestCount(String ipAddress) {
        return requestMap.getOrDefault(
                ipAddress, new RequestInfo()).getRequestCount();
    }

    public static String getUserAgent(String ipAddress) {
        return requestMap.getOrDefault(
                ipAddress, new RequestInfo()).getUserAgent();
    }

    public static void clearExpiredRequests() {
        Instant oneHourAgo = Instant.now().minusSeconds(3600);
        requestMap.entrySet().removeIf(
                entry -> entry.getValue().getRequestTime().isBefore(oneHourAgo)
        );
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    private static class RequestInfo {
        private Instant requestTime;
        private int requestCount;
        private String userAgent;
    }
}
