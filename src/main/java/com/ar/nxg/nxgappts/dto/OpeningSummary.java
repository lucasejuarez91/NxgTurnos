package com.ar.nxg.nxgappts.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
public class OpeningSummary {
    private DayOfWeek firstDay;
    private DayOfWeek lastDay;
    private LocalTime startTime;
    private LocalTime endTime;

    public OpeningSummary(DayOfWeek firstDay, DayOfWeek lastDay, LocalTime startTime, LocalTime endTime) {
        this.firstDay = firstDay;
        this.lastDay = lastDay;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

