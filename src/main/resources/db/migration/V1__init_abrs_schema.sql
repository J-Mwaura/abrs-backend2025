CREATE TABLE flight (
                        id BIGSERIAL PRIMARY KEY,

                        flight_number VARCHAR(20) NOT NULL,
                        departure_airport VARCHAR(10) NOT NULL,
                        arrival_airport VARCHAR(10) NOT NULL,
                        departure_time TIMESTAMPTZ NOT NULL,

                        status VARCHAR(30) NOT NULL DEFAULT 'CREATED',

                        CONSTRAINT uk_flight_number_departure
                            UNIQUE (flight_number, departure_time)
);

CREATE TABLE boarding_sequence (
                                   id BIGSERIAL PRIMARY KEY,

                                   flight_id BIGINT NOT NULL,
                                   sequence_number INTEGER NOT NULL,

                                   status VARCHAR(30) NOT NULL DEFAULT 'EXPECTED',
                                   note TEXT,

                                   CONSTRAINT fk_sequence_flight
                                       FOREIGN KEY (flight_id)
                                           REFERENCES flight(id)
                                           ON DELETE CASCADE,

                                   CONSTRAINT uk_flight_sequence
                                       UNIQUE (flight_id, sequence_number)
);
CREATE TABLE staff_user (
                            id BIGSERIAL PRIMARY KEY,

                            username VARCHAR(100) NOT NULL,
                            role VARCHAR(50) NOT NULL,
                            is_active BOOLEAN NOT NULL DEFAULT TRUE,

                            CONSTRAINT uk_staff_username UNIQUE (username)
);

CREATE TABLE boarding_event (
                                id BIGSERIAL PRIMARY KEY,

                                sequence_id BIGINT NOT NULL,
                                event_type VARCHAR(50) NOT NULL,
                                event_time TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                                staff_user_id BIGINT,
                                details TEXT,

                                CONSTRAINT fk_event_sequence
                                    FOREIGN KEY (sequence_id)
                                        REFERENCES boarding_sequence(id)
                                        ON DELETE CASCADE,

                                CONSTRAINT fk_event_staff
                                    FOREIGN KEY (staff_user_id)
                                        REFERENCES staff_user(id)
                                        ON DELETE SET NULL
);

CREATE INDEX idx_sequence_flight
    ON boarding_sequence(flight_id);

CREATE INDEX idx_sequence_status
    ON boarding_sequence(status);

CREATE INDEX idx_event_sequence
    ON boarding_event(sequence_id);

CREATE INDEX idx_event_type
    ON boarding_event(event_type);
