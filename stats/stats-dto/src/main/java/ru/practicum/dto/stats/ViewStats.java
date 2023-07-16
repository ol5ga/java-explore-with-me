package ru.practicum.dto.stats;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;

}

