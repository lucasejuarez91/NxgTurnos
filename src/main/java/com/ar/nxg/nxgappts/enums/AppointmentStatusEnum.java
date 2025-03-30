package com.ar.nxg.nxgappts.enums;

import lombok.Getter;

@Getter
public enum AppointmentStatusEnum {
    CREATED("title.status.created"),
    CONFIRM("title.status.created"),
    IN_PROGRESS("title.status.created"),
    CANCELLED("title.status.created"),
    COMPLETED("title.status.created");

    private final String text;

    AppointmentStatusEnum(String text) {
        this.text = text;
    }

}
