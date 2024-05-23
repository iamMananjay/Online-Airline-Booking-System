package com.airtravel.airtravel.service;


import com.airtravel.airtravel.model.Flight;
import com.airtravel.airtravel.model.Seat;
import com.airtravel.airtravel.repository.FlightRepository;
import com.airtravel.airtravel.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService {
    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private FlightRepository flightRepository;


    @Override
    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }


    @Override
    public void updateSeat(Seat seat) {
        seatRepository.save(seat);
    }

    public void lockSeat(String seatNumber) {
        Optional<Seat> seatOptional = seatRepository.findBySeatNumber(seatNumber);
        if (seatOptional.isPresent()) {
            Seat seat = seatOptional.get();
            seat.setLocked(true);
            seat.setLockedAt(LocalDateTime.now());
            seatRepository.save(seat);
        }
    }

    public void unlockSeat(String seatNumber) {
        Optional<Seat> seatOptional = seatRepository.findBySeatNumber(seatNumber);
        if (seatOptional.isPresent()) {
            Seat seat = seatOptional.get();
            seat.setLocked(false);
            seat.setLockedAt(null);
            seatRepository.save(seat);
        }
    }

@Scheduled(fixedRate = 60000) // Runs every minute
public void unlockExpiredSeats() {
    List<Seat> lockedSeats = seatRepository.findByLockedTrue();
    LocalDateTime now = LocalDateTime.now();
    for (Seat seat : lockedSeats) {
        if (seat.getLockedAt().plusMinutes(1).isBefore(now)) {
            seat.setLocked(false);
            seat.setLockedAt(null);
            seatRepository.save(seat);
        }
    }
}




    @Override
    public Map<String, Boolean> convertSeatsToMap(List<Seat> seats) {
        Map<String, Boolean> seatMap = new HashMap<>();
        for (Seat seat : seats) {
            seatMap.put(seat.getSeatNumber(), seat.isAvailable());
        }
        return seatMap;
    }

    @Override
    public List<Seat> getSeatsByFlightId(Long flightId) {
        return seatRepository.findByFlightFlightId(flightId);
    }

    @Override
    public Seat getSeatByNumberAndFlight(String seatNumber, Flight flight) {
        return null;
    }

    @Override
    public void updateSeatAvailability(String flightNumber, String seatNumber, boolean availability) {

    }

    //    @Override
//    public Seat getSeatByNumberAndFlight(String seatNumber, Flight flight) {
//        return seatRepository.findBySeatNumberAndFlight(seatNumber, flight);
//    }
//
//    @Override
//    public void updateSeatAvailability(String flightNumber, String seatNumber, boolean availability) {
//        Flight flight = flightRepository.findByFlightNumber(flightNumber);
//        Seat seat = seatRepository.findBySeatNumberAndFlight(seatNumber, flight);
//        seat.setAvailable(availability);
//        seatRepository.save(seat);
//    }
    @Override
    public Seat getSeatByNumberAndFlight(String seatNumber, String flightNumber) {
        return seatRepository.findBySeatNumberAndFlightNumber(seatNumber, flightNumber).orElse(null);
    }


}
