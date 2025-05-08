package com.edge.template.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class HeartbeatService {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatService.class);

    /**
     * Generates heartbeat data with status and timestamp
     * 
     * @return Map containing heartbeat information
     */
    public Map<String, Object> getHeartbeatData() {
        logger.debug("Generating heartbeat data");
        
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> heartbeatData = new HashMap<>();
        
        heartbeatData.put("status", "UP");
        heartbeatData.put("timestamp", now.toString());
        heartbeatData.put("environment", getEnvironment());
        
        // Log at multiple levels to test logging functionality
        logger.trace("Heartbeat trace logging: {}", heartbeatData);
        logger.debug("Heartbeat debug logging: {}", heartbeatData);
        logger.info("Heartbeat status is {}", heartbeatData.get("status"));
        
        return heartbeatData;
    }
    
    /**
     * Determines the current environment 
     * This is a placeholder - in a real app would use Spring profiles or env vars
     */
    private String getEnvironment() {
        logger.debug("Determining current environment");
        return "development";
    }
}