//package com.vong.manidues.config.trackingip;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.lang.NonNull;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//// 매 요청 마다 필터를 거치도록 extends
//public class IpRequestFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(
//            @NonNull HttpServletRequest request,
//            @NonNull HttpServletResponse response,
//            @NonNull FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String authHeader = request.getHeader("Authorization");
//        String requestedRemoteAddress = request.getRemoteAddr();
//
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//
//            RequestTracker.trackRequest(requestedRemoteAddress, request);
//            log.info("\nrequest from {} count is: {}\nrequested User-Agent is: {}\nrequested Locale is: {}\n",
//                    requestedRemoteAddress,
//                    RequestTracker.getRequestCount(requestedRemoteAddress),
//                    RequestTracker.getUserAgent(requestedRemoteAddress),
//                    request.getLocale()
//            );
//
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
