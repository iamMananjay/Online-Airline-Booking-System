package com.airtravel.airtravel.repository;

import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
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



}
