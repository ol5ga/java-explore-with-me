package ru.practicum.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EndpointHit {
    private String app;
    private String uri;
    private String ip;
    private String timestamp;

}
