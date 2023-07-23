CREATE TABLE IF NOT EXISTS category (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(50) NOT NULL UNIQUE,
  CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  name VARCHAR(250) NOT NULL,
  email VARCHAR(254) NOT NULL UNIQUE,
    CONSTRAINT pk_users PRIMARY KEY (id)

);

CREATE TABLE IF NOT EXISTS location(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    lat REAL NOT NULL,
    lon REAL NOT NULL,
      CONSTRAINT pk_location PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS events(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description VARCHAR(7000) NOT NULL,
    event_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    paid boolean,
    participant_limit BIGINT,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state VARCHAR(9),
    title VARCHAR(120) NOT NULL,
    views BIGINT,
    CONSTRAINT pk_event PRIMARY KEY (id),
    CONSTRAINT fk_cat_id_to_category
    FOREIGN KEY (category_id) REFERENCES category (id),
    CONSTRAINT fk_initiator_id_to_user
    FOREIGN KEY (initiator_id) REFERENCES users (id),
    CONSTRAINT location_id_to_location
    FOREIGN KEY (location_id) REFERENCES location (id)
);

CREATE TABLE IF NOT EXISTS requests(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    requester_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id BIGINT NOT NULL,
    state VARCHAR(9),
    CONSTRAINT pk_request PRIMARY KEY (id),
    CONSTRAINT requester_id_to_user
    FOREIGN KEY (requester_id) REFERENCES users (id),
    CONSTRAINT event_id_to_event
    FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE IF NOT EXISTS compilations(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    event_id BIGINT,
    pinned BOOLEAN,
    title VARCHAR(50) NOT NULL UNIQUE,
    CONSTRAINT pk_compilations PRIMARY KEY (id),
    CONSTRAINT event_id_to_compilations
    FOREIGN KEY (event_id) REFERENCES events (id)
);
CREATE TABLE IF NOT EXISTS compilation_event (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY(compilation_id, event_id),
    FOREIGN KEY(compilation_id) REFERENCES compilations(id) ON DELETE CASCADE,
    FOREIGN KEY(event_id) REFERENCES events(id) ON DELETE CASCADE
);



