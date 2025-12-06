package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping("/port")
    public String getPort() {
        logger.info("InfoController. Активный профиль: {}, Port: {}", activeProfile, serverPort);
        return "Active profile: " + activeProfile + ", Server port: " + serverPort;
    }
}