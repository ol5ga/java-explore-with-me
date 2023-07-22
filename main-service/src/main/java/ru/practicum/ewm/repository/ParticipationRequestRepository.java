package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.event.Event;
import ru.practicum.ewm.model.request.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest,Long> {

    List<ParticipationRequest> findAllByEventAndState(Event event, String state);

    ParticipationRequest findFirstByEvent_IdAndRequester_Id(long eventId, long userId);
}
