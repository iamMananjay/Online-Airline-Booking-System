package com.airtravel.airtravel.util;

import java.util.ArrayList;
import java.util.List;

public class SeatingPlan {
    private List<List<Integer>> seatingPlan;

    public SeatingPlan(int rows, int cols) {
        seatingPlan = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                row.add(0); // 0 represents an available seat
            }
            seatingPlan.add(row);
        }
    }

    public List<List<Integer>> getSeatingPlan() {
        return seatingPlan;
    }
}
