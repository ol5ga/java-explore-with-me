package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.stats.StatsClient;
import ru.practicum.dto.stats.ViewStats;
import ru.practicum.ewm.model.event.Event;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class ViewService {
    private StatsClient statsClient;
    public Map<Long, Integer> getViews(List<Event> result) {
        if (result.isEmpty()) {
            return Collections.emptyMap();
        }
        String[] uris = result.stream()
                .map(e -> e.getId())
                .map(e -> String.format("/events/%d", e))
                .toArray(String[]::new);
        List<ViewStats> stats = statsClient.getStats(LocalDateTime.now().minusYears(1), LocalDateTime.now(), uris, true);
        Map<Long, Integer> views = new HashMap<>();
        for (ViewStats view : stats) {
            String index = view.getUri().substring(8);
            views.put(Long.parseLong(index), Math.toIntExact(view.getHits()));
        }
        return views;
    }
}
