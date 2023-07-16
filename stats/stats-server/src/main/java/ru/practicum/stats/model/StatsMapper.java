package ru.practicum.stats.model;

import ru.practicum.dto.stats.EndpointHit;
import ru.practicum.dto.stats.ViewStats;

public class StatsMapper {

    public static Stats toStats(EndpointHit hit){
        return Stats.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .timestamp(hit.getTimestamp()).build();
    }

}