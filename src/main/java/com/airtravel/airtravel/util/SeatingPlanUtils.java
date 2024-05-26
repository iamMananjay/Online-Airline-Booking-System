package com.airtravel.airtravel.util;

import java.util.ArrayList;
import java.util.List;

public class SeatingPlanUtils {

    public static boolean isSeatAvailable(List<List<Integer>> seatingPlan, int row, int col) {
        return seatingPlan.get(row).get(col) == 0;
    }

    public static void reserveSeats(List<List<Integer>> seatingPlan, List<int[]> seats) {
        for (int[] seat : seats) {
            seatingPlan.get(seat[0]).set(seat[1], 1); // 1 represents a reserved seat
        }
    }

    public static List<int[]> findContiguousSeats(List<List<Integer>> seatingPlan, int row, int numSeats) {
        List<int[]> availableSeats = new ArrayList<>();
        int consecutiveCount = 0;

        for (int col = 0; col < seatingPlan.get(row).size(); col++) {
            if (isSeatAvailable(seatingPlan, row, col)) {
                consecutiveCount++;
                availableSeats.add(new int[]{row, col});
                if (consecutiveCount == numSeats) {
                    return availableSeats;
                }
            } else {
                consecutiveCount = 0;
                availableSeats.clear();
            }
        }
        return new ArrayList<>(); // Return an empty list if no contiguous seats found
    }
}
