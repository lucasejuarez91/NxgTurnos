package com.ar.nxg.nxgappts.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class QrAppointmentDTO {
    private String bookingId;
    private String encryptedCode;
    private String client;
    private LocalDateTime startsAt;

}

