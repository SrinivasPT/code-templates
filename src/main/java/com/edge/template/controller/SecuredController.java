package com.edge.template.controller;

import com.edge.template.util.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/secure")
public class SecuredController {

    private static final Logger logger = LoggerFactory.getLogger(SecuredController.class);

    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        logger.info("User info requested by {} | TraceId: {}", 
                authentication.getName(), 
                TraceContext.getTraceId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("traceId", TraceContext.getTraceId());  // Include trace ID in response
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin-only")
    public ResponseEntity<Map<String, Object>> adminOnly() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        logger.info("Admin-only endpoint accessed by {} | TraceId: {}", 
                authentication.getName(), 
                TraceContext.getTraceId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is an admin-only endpoint");
        response.put("username", authentication.getName());
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("traceId", TraceContext.getTraceId());  // Include trace ID in response
        
        return ResponseEntity.ok(response);
    }
}