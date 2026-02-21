package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "InfoController", description = "Информация о Релизе и Порте работы")
public class InfoController {

    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @GetMapping("/port")
    @Operation(summary = "Тип релиза и Порт")
    public String getPort() {
        logger.info("InfoController. Активный профиль: {}, Port: {}", activeProfile, serverPort);
        return "Active profile: " + activeProfile + ", Server port: " + serverPort;
    }
}