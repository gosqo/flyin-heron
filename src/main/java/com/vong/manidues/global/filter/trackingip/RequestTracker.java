package com.vong.manidues.global.filter.trackingip;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RequestTracker {

    public static final ConcurrentHashMap<String, RequestInfo> requestMap = new ConcurrentHashMap<>();

    public static String getWholeRequestMap() {
        return String.format("requestMap:\n%s", requestMap);
    }

    public static void trackRequest(HttpServletRequest request) {
        String requestIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String connection = request.getHeader("Connection");
        Instant requestTime = requestMap.get(requestIp) == null
                ? Instant.now()
                : requestMap.get(requestIp).getRequestTime();
        int requestCount = requestMap.getOrDefault(
                requestIp, new RequestInfo()).getRequestCount() + 1;

        requestMap.put(requestIp, new RequestInfo(
                requestTime,
                requestCount,
                userAgent,
                connection
        ));
    }

    public static int getRequestCount(String ipAddress) {
        return requestMap.getOrDefault(
                ipAddress, new RequestInfo()).getRequestCount();
    }

    public static void clearExpiredRequests() {
        Instant tenSecondsAgo = Instant.now().minusSeconds(10);
        requestMap.entrySet().removeIf(
                entry -> entry.getValue().getRequestTime().isBefore(tenSecondsAgo)
        );
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class RequestInfo {
        private Instant requestTime;
        private int requestCount;
        private String userAgent;
        private String connection;

        @Override
        public String toString() {
            return String.format("""

                                First request time: %S
                                RequestCount: %d
                                User-Agent: %s
                                Connection: %s
                                
                            """
                    , this.requestTime.atZone(ZoneId.of("Asia/Seoul"))
                            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    , this.requestCount
                    , this.userAgent
                    , this.connection
            );
        }
    }
}
