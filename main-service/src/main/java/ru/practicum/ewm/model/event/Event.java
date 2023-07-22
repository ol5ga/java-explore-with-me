package ru.practicum.ewm.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.model.location.Location;
import ru.practicum.ewm.model.request.ParticipationRequest;
import ru.practicum.ewm.model.category.Category;
import ru.practicum.ewm.model.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String annotation;
    @JoinColumn(name = "category_id")
    @ManyToOne
    private Category category;
//    @JoinColumn(name ="requests_id")
//    @OneToMany
//    private List<ParticipationRequest> requests;
    @Column(name ="created_on")
    private LocalDateTime createdOn;
    @Column
    private String description;
    @Column(name ="event_date")
    private LocalDateTime eventDate;
    @JoinColumn(name ="initiator_id")
    @OneToOne
    private User initiator;
    @JoinColumn(name ="location_id")
    @OneToOne
    private Location location;
    @Column
    private Boolean paid;
    @Column(name ="participant_limit")
    private Integer participantLimit;
    @Column(name ="published_on")
    private LocalDateTime publishedOn;
    @Column(name ="request_moderation")
    private Boolean requestModeration;
    @Column
    private String state;
    @Column
    private String title;
    @Column
    private Integer views;
}

