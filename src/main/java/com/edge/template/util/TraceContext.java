package com.edge.template.util;

import org.slf4j.MDC;
import java.util.UUID;

/**
 * Utility class for managing distributed tracing context across application layers.
 * This allows for correlating log messages from controllers through services to repositories.
 */
public class TraceContext {

    public static final String REQUEST_ID = "requestId";
    public static final String USER_ID = "userId";
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String PARENT_SPAN_ID = "parentSpanId";
    public static final String COMPONENT = "component";

    /**
     * Initialize a new trace context with a unique trace ID.
     * Usually called at the entry point of a request.
     * 
     * @param userId the user identifier
     * @return the generated trace ID
     */
    public static String initTrace(String userId) {
        String traceId = UUID.randomUUID().toString();
        String spanId = UUID.randomUUID().toString().substring(0, 8);
        
        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, spanId);
        MDC.put(USER_ID, userId);
        
        return traceId;
    }
    
    /**
     * Start a new span within the current trace.
     * Call this method when entering a new logical component (service, repository).
     * 
     * @param componentName name of the component being entered (e.g., "UserService", "OrderRepository")
     * @return the new span ID
     */
    public static String startSpan(String componentName) {
        String parentSpanId = MDC.get(SPAN_ID);
        String newSpanId = UUID.randomUUID().toString().substring(0, 8);
        
        if (parentSpanId != null) {
            MDC.put(PARENT_SPAN_ID, parentSpanId);
        }
        
        MDC.put(SPAN_ID, newSpanId);
        MDC.put(COMPONENT, componentName);
        
        return newSpanId;
    }
    
    /**
     * End the current span and restore the parent span context.
     * Call this method when exiting a component.
     */
    public static void endSpan() {
        String parentSpanId = MDC.get(PARENT_SPAN_ID);
        
        if (parentSpanId != null) {
            MDC.put(SPAN_ID, parentSpanId);
            MDC.remove(PARENT_SPAN_ID);
        }
        
        MDC.remove(COMPONENT);
    }
    
    /**
     * Get the current trace ID.
     * 
     * @return the current trace ID or null if not set
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }
    
    /**
     * Get the current span ID.
     * 
     * @return the current span ID or null if not set
     */
    public static String getSpanId() {
        return MDC.get(SPAN_ID);
    }
    
    /**
     * Clear all trace context from MDC.
     * Usually called at the end of a request.
     */
    public static void clearTrace() {
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
        MDC.remove(PARENT_SPAN_ID);
        MDC.remove(COMPONENT);
    }
}