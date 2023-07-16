package ru.practicum.stats.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.stats.EndpointHit;
import ru.practicum.dto.stats.ViewStats;
import ru.practicum.stats.exceptions.ValidationException;
import ru.practicum.stats.model.Stats;
import ru.practicum.stats.model.StatsMapper;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Data
@AllArgsConstructor
public class StatsService {

  private final StatsRepository statsRepository;

    public void add(EndpointHit hit){
        Stats stats = StatsMapper.toStats(hit);
        statsRepository.save(stats);
    }

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique){
        if (start.isAfter(end) || start.isEqual(end))
            throw new ValidationException("Некоректно указан интервал поиска");
        List<ViewStats> stats;
        if(uris == null || uris.isEmpty()){
            if(unique){
                stats = statsRepository.getStatsUnique(start,end);
            } else {
                stats = statsRepository.getStats(start, end);
            }
        } else {
                if (unique) {
                    stats = statsRepository.getStatsForUriUnique(start, end, uris);
                } else {
                    stats = statsRepository.getStatsForUri(start, end, uris);
                }

        }
        return stats;
    }

    public List<Stats> getAll(){
        return statsRepository.findAll();
    }

}
