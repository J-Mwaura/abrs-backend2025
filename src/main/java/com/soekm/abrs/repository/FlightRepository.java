package com.soekm.abrs.repository;

import com.soekm.abrs.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightNumberAndDepartureTime(
            String flightNumber,
            ZonedDateTime departureTime
    );

    boolean existsByFlightNumberAndFlightDate(String flightNumber, LocalDate flightDate);}
