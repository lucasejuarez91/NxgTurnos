package com.ar.nxg.nxgappts.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BookingDTO implements Serializable {
    private Long salonId;
    private Long serviceId;
    private Long professionalId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
}
