package com.airtravel.airtravel.repository;

import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    Optional<Seat> findBySeatNumber(String seatNumber);
    List<Seat> findByFlightFlightId(Long flightId);

    @Transactional
    void deleteByFlight_FlightId(Long flightId); // Use JPQL query to delete seats by flightId

    List<Seat> findByFlight_FlightNumber(String flightNumber);
    Optional<Seat> findByFlight_FlightNumberAndSeatNumber(String flightNumber, String seatNumber);
    Seat findBySeatNumberAndFlight(String seatNumber, Flight flight);

    Optional<Seat> findBySeatNumberAndFlightNumber(String seatNumber, String flightNumber);

    List<Seat> findByLockedTrue();

    @Query("select s from Seat s where s.flightNumber = ?1 and s.available = true")
    List<Seat> findByFlightNumberAndIsAvailableTrue(String flightNumber);

//
    List<Seat> findByFlightNumberAndSeatNumberIn(String flightNumber, List<String> selectedSeats);
}
