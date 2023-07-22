package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest,Long> {

    List<ParticipationRequest> findAllByEventAndStateOrderByCreated(Event event, String state);

    List<ParticipationRequest> findAllByEvent(Event event);

    ParticipationRequest findFirstByEvent_IdAndRequester_Id(long eventId, long userId);
}
