package com.vong.manidues;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Main {
    public static void main(String[] args) {
        RequestTracker.trackRequest("key1", "hello");
        RequestTracker.trackRequest("key1", "hello2");
        RequestTracker.trackRequest("key1", "hello3");
        RequestTracker.trackRequest("key1", "hello");
        RequestTracker.trackRequest("key2", "hello2");
        RequestTracker.trackRequest("key2", "hello3");
        log.info(RequestTracker.getRequestMap());
        log.info(RequestTracker.requestMapToString());

    }
}

class RequestTracker {
    private static final ConcurrentHashMap<String, RequestInfo> requestMap =
            new ConcurrentHashMap<>();

    public static String getRequestMap() { return requestMap.toString(); }

    public static String requestMapToString() {
        return """
                
                requestMap is like:
                """ + RequestTracker.requestMap + """
                """;
    }

    public static void trackRequest(String key, String name) {
        int count = requestMap.getOrDefault(key, new RequestInfo())
                .getNumber() + 1;
        requestMap.put(key, new RequestInfo(name, count, Instant.now()));
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class RequestInfo {
        private String name;
        private int number;
        private Instant time;

        @Override
        public String toString() {
            return """
                    
                    RequestInfo(
                        name=""" + this.name + """
                    ,
                        number=""" + this.number + """
                    ,
                        time=""" + this.time + """
                    
                    """;
//                    "RequestInfo{" +
//                    "name='" + name + '\'' +
//                    ", number=" + number +
//                    ", time=" + time +
//                    '}';
        }
    }
}

