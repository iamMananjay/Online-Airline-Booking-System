package com.airtravel.airtravel.service;

import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface SeatService {
    List<Seat> getAllSeats();
    void lockSeat(String seatNumber);
    void unlockSeat(String seatNumber);
    Map<String, Boolean> convertSeatsToMap(List<Seat> seats);
    List<Seat> getSeatsByFlightId(Long flightId);
    Seat getSeatByNumberAndFlight(String seatNumber, Flight flight);
    void updateSeatAvailability(String flightNumber, String seatNumber, boolean availability);
//    @Query("SELECT s FROM Seat s WHERE s.seatNumber = :seatNumber AND s.flightNumber = :flightNumber AND s.flightId IS NOT NULL")
//    Seat getSeatByNumberAndFlight(@Param("seatNumber") String seatNumber, @Param("flightNumber") String flightNumber);
//

    @Query("SELECT s FROM Seat s WHERE s.seatNumber = :seatNumber AND s.flightNumber = :flightNumber AND s.flightId IS NOT NULL")
    Seat getSeatByNumberAndFlight(@Param("seatNumber") String seatNumber, @Param("flightNumber") String flightNumber);


    void updateSeat(Seat seat);



}
