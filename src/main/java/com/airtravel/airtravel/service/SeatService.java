package com.airtravel.airtravel.service;

import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;

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
    Seat getSeatByNumberAndFlight(String seatNumber, String flightNumber);

    void updateSeat(Seat seat);


}
