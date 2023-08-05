package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.request.ParticipationState;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByEventAndStatusOrderByCreated(Event event, ParticipationState state);

    List<ParticipationRequest> findAllByEvent(Event event);

    List<ParticipationRequest> findAllByRequester_Id(long userId);

    ParticipationRequest findFirstByEvent_IdAndRequester_Id(long eventId, long userId);

    List<ParticipationRequest> findAllByEventIn(List<Event> events);
}
