package com.edge.template.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.edge.template.service.HeartbeatService;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HeartbeatController {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatController.class);
    
    private final HeartbeatService heartbeatService;
    
    @Autowired
    public HeartbeatController(HeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    @GetMapping("/heartbeat")
    public ResponseEntity<Map<String, Object>> heartbeat() {
        logger.info("Heartbeat check requested at {}", LocalDateTime.now());
        
        Map<String, Object> response = heartbeatService.getHeartbeatData();
        
        logger.debug("Returning heartbeat response: {}", response);
        return ResponseEntity.ok(response);
    }
}
