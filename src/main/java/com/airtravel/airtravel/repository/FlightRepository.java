package com.airtravel.airtravel.repository;

import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByDepartureAirportAndDestinationAirport(String departureAirport, String destinationAirport);
    void deleteByFlightId(Long flightId);
    Flight findByFlightNumber(String flightNumber);


}
