package ru.practicum.stats.model;

import ru.practicum.dto.stats.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StatsMapper {

    public static Stats toStats(EndpointHit hit) {
        return Stats.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(LocalDateTime.parse(hit.getTimestamp(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        .build();
    }

}