package com.currycookie.data;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DailySummary {
    private LocalDate date;
    private double minTemp;
    private double maxTemp;
    private String desription;
}
