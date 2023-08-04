package ru.practicum.ewm.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.client.stats.StatsClient;
import ru.practicum.dto.stats.ViewStats;
import ru.practicum.ewm.model.event.Event;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class ViewService {
    private StatsClient statsClient;
    public Map<Long, Integer> getViews(List<Event> result) {
        if (result.isEmpty()|| result.get(0).getPublishedOn() == null) {
            return Collections.emptyMap();
        }
        LocalDateTime startDate;
        if(result.size() > 1) {
            startDate = result.stream()
                    .map(Event::getPublishedOn)
                    .min(LocalDateTime::compareTo)
                    .get();
        } else {
            startDate = result.get(0).getPublishedOn();
        }
        String[] uris = result.stream()
                .map(e -> e.getId())
                .map(e -> String.format("/events/%d", e))
                .toArray(String[]::new);
        List<ViewStats> stats = statsClient.getStats(startDate.minusMinutes(1), LocalDateTime.now(), uris, true);
        Map<Long, Integer> views = new HashMap<>();
        for (ViewStats view : stats) {
            String index = view.getUri().substring(8);
            views.put(Long.parseLong(index), Math.toIntExact(view.getHits()));
        }
        return views;
    }
}
