package com.airtravel.airtravel.util;

import org.springframework.stereotype.Service;

@Service
public class SeatingService {

    public void updateSeatingPlan(Aircraft aircraft) {
        SeatingAlgorithm algorithm = new SeatingAlgorithm();
        algorithm.preventSingleSeatScatter(aircraft);
    }

    // Other methods like bookSeat, cancelSeat, etc.
}

