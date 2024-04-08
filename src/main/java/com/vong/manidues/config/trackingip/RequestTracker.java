package com.vong.manidues.config.trackingip;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class RequestTracker {

    private static final ConcurrentHashMap<String, RequestInfo> requestMap =
            new ConcurrentHashMap<>();

    public static String getRequestMap() {
        return """
                requestMap:
                \t""" + requestMap + """
                                
                """;
    }

    public static void trackRequest(HttpServletRequest request) {
        String requestIp = request.getRemoteAddr();
        Instant requestTime = requestMap.get(requestIp) == null
                ? Instant.now()
                : requestMap.get(requestIp).getRequestTime();
        int requestCount = requestMap.getOrDefault(
                requestIp, new RequestInfo()).getRequestCount() + 1;
        String requestUserAgent = request.getHeader("User-Agent");
        String connection = request.getHeader("Connection");

        requestMap.put(requestIp, new RequestInfo(
                requestTime,
                requestCount,
                requestUserAgent,
                connection
        ));
    }

    public static int getRequestCount(String ipAddress) {
        return requestMap.getOrDefault(
                ipAddress, new RequestInfo()).getRequestCount();
    }

    public static Instant getRequestTime(String ipAddress) {
        return requestMap.getOrDefault(
                ipAddress, new RequestInfo()).getRequestTime();
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
    private static class RequestInfo {
        private Instant requestTime;
        private int requestCount;
        private String userAgent;
        private String connection;

        @Override
        public String toString() {
            return """
                    =>
                            Request time: """ + this.requestTime.atZone(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + """
                    ,
                            Request Count: """ + this.requestCount + """
                    ,
                            User-Agent: """ + this.userAgent + """
                    ,
                            Connection: """ + this.connection + """
                                        
                    \t""";
        }
    }
}
