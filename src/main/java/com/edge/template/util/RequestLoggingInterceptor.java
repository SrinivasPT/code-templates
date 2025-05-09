package com.edge.template.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor that logs HTTP requests and adds request context to the MDC.
 * Updated to support distributed tracing across application layers.
 * 
 * Supports receiving trace IDs from Angular clients via the X-Trace-ID header,
 * which enables end-to-end tracing across the full stack.
 */
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingInterceptor.class);
    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final String[] INTERESTING_HEADERS = {
            "User-Agent", "Referer", "X-Forwarded-For", "X-Real-IP", 
            "Origin", "Accept", "Accept-Language", "Content-Type"
    };    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // Store request start time for performance tracking
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        
        // Add the authenticated user ID if available from Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
        MDC.put("userId", userId);
        
        // Check if client sent a trace ID in the header
        String clientTraceId = request.getHeader(TraceContext.TRACE_ID_HEADER);
        
        // Initialize trace context for distributed tracing, using the client's trace ID if available
        TraceContext.initTrace(userId, clientTraceId);
        TraceContext.startSpan("Controller");
          // Log whether we're using a client-provided trace ID
        if (clientTraceId != null && !clientTraceId.trim().isEmpty()) {
            logger.debug("Using client-provided trace ID: {}", clientTraceId);
        }
        
        // Extract PingFederate session reference or token identifier (if available)
        // This maintains correlation without using HttpSession
        String pingFedSessionRef = extractPingFedSessionReference(request);
        MDC.put("sessionRef", pingFedSessionRef);
        
        // Capture client information
        String userAgent = request.getHeader("User-Agent");
        String clientIp = getClientIpAddress(request);
        String forwardedFor = request.getHeader("X-Forwarded-For");
        
        MDC.put("userAgent", userAgent != null ? userAgent : "unknown");
        MDC.put("clientIp", clientIp);
        
        // Log the beginning of the request with detailed information
        Map<String, String> headerMap = extractInterestingHeaders(request);
        
        logger.info("Received request: {} {} | Client: {} | IP: {} | Forwarded: {} | User: {} | SessionRef: {} | Headers: {} | TraceId: {}", 
                request.getMethod(), 
                request.getRequestURI(),
                userAgent != null ? userAgent : "unknown",
                clientIp,
                forwardedFor != null ? forwardedFor : "none",
                userId,
                pingFedSessionRef,
                headerMap,
                TraceContext.getTraceId());
        
        return true;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        logger.debug("Request processed: {} {} with status {} | TraceId: {}", 
                request.getMethod(), 
                request.getRequestURI(), 
                response.getStatus(),
                TraceContext.getTraceId());
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        long processingTime = startTime != null ? System.currentTimeMillis() - startTime : -1;
        
        MDC.put("processingTimeMs", String.valueOf(processingTime));
        MDC.put("statusCode", String.valueOf(response.getStatus()));
        
        if (ex != null) {
            logger.error("Error processing request: {} {} | Status: {} | Time: {}ms | Error: {} | TraceId: {}", 
                    request.getMethod(), 
                    request.getRequestURI(),
                    response.getStatus(),
                    processingTime,
                    ex.getMessage(),
                    TraceContext.getTraceId(),
                    ex);
        } else {
            logger.info("Request completed: {} {} | Status: {} | Time: {}ms | TraceId: {}", 
                    request.getMethod(), 
                    request.getRequestURI(),
                    response.getStatus(),
                    processingTime,
                    TraceContext.getTraceId());
        }
        
        // End the controller span
        TraceContext.endSpan();
        
        // Clear all trace context and MDC to prevent leaking into subsequent requests
        TraceContext.clearTrace();
        MDC.clear();
    }
    
    /**
     * Extracts the client IP address considering proxy headers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // For multiple IP addresses forwarded, take the first one (client's IP)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
    
    /**
     * Extract headers of interest for logging purposes
     */
    private Map<String, String> extractInterestingHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        
        for (String headerName : INTERESTING_HEADERS) {
            String value = request.getHeader(headerName);
            if (value != null) {
                headerMap.put(headerName, value);
            }
        }
        
        return headerMap;
    }
    
    /**
     * Extracts PingFederate session reference from request headers
     * Looks for standard headers used by PingFederate for session tracking
     */
    private String extractPingFedSessionReference(HttpServletRequest request) {
        // Try to get PingFederate session reference from common header locations
        String sessionRef = request.getHeader("pingfed-session-ref");
        if (sessionRef == null || sessionRef.isEmpty()) {
            sessionRef = request.getHeader("X-PingFed-SessionRef");
        }
        if (sessionRef == null || sessionRef.isEmpty()) {
            // Extract from Authorization Bearer token if present (JWT might contain session info)
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // In a real implementation, you might decode the JWT and extract a session identifier
                // This is a simplified placeholder
                sessionRef = "token-ref-" + authHeader.hashCode();
            }
        }
        
        return sessionRef != null ? sessionRef : "no-session-ref";
    }
}