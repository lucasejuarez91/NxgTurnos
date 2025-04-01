package com.ar.nxg.nxgappts.enums;

import lombok.Getter;

@Getter
public enum AppointmentStatusEnum {
    CREATED("title.status.created"),
    CONFIRM("title.status.confirm"),
    IN_PROGRESS("title.status.in_progress"),
    CANCELLED("title.status.cancelled"),
    COMPLETED("title.status.completed");

    private final String text;

    AppointmentStatusEnum(String text) {
        this.text = text;
    }

}
