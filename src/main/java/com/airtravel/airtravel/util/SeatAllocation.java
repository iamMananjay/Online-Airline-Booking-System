package com.airtravel.airtravel.util;

import java.util.ArrayList;
import java.util.List;

public class SeatAllocation {

    public static List<int[]> allocateSeats(List<List<Integer>> seatingPlan, int numSeats) {
        for (int row = 0; row < seatingPlan.size(); row++) {
            List<int[]> contiguousSeats = SeatingPlanUtils.findContiguousSeats(seatingPlan, row, numSeats);
            if (!contiguousSeats.isEmpty()) {
                SeatingPlanUtils.reserveSeats(seatingPlan, contiguousSeats);
                return contiguousSeats;
            }
        }
        // If no contiguous seats found, apply fallback strategy
        return fallbackSeatingStrategy(seatingPlan, numSeats);
    }

    private static List<int[]> fallbackSeatingStrategy(List<List<Integer>> seatingPlan, int numSeats) {
        List<int[]> allocatedSeats = new ArrayList<>();
        int seatsNeeded = numSeats;

        for (int row = 0; row < seatingPlan.size(); row++) {
            for (int col = 0; col < seatingPlan.get(row).size(); col++) {
                if (seatsNeeded == 0) {
                    break;
                }
                if (SeatingPlanUtils.isSeatAvailable(seatingPlan, row, col)) {
                    allocatedSeats.add(new int[]{row, col});
                    SeatingPlanUtils.reserveSeats(seatingPlan, List.of(new int[]{row, col}));
                    seatsNeeded--;
                }
            }
        }

        // Check for scattered single seats and try to re-optimize
        if (seatsNeeded == 0) {
            optimizeSingleSeats(seatingPlan);
            return allocatedSeats;
        } else {
            // Handle case where not enough seats are available
            return new ArrayList<>();
        }
    }

    private static void optimizeSingleSeats(List<List<Integer>> seatingPlan) {
        // Implement logic to find a better seat arrangement
    }

    private static boolean isSingleScattered(List<List<Integer>> seatingPlan, int row, int col) {
        // Check surrounding seats to determine if it's a single scattered seat
        int emptyCount = 0;
        int[][] surroundingSeats = {{row-1, col}, {row+1, col}, {row, col-1}, {row, col+1}};
        for (int[] seat : surroundingSeats) {
            int r = seat[0], c = seat[1];
            if (r >= 0 && r < seatingPlan.size() && c >= 0 && c < seatingPlan.get(row).size()) {
                if (seatingPlan.get(r).get(c) == 0) {
                    emptyCount++;
                }
            }
        }
        return emptyCount >= 3; // Adjust the threshold as needed
    }
}
