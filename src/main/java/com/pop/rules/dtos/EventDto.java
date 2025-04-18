package com.pop.rules.dtos;

import com.pop.rules.enums.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

@Data
public class EventDto {

    @Schema(example = "DEPOSIT")
    private EventType eventType;

    @Schema(example = "500")
    private Double amount;

    @Schema(example = "1")
    private Long userId;

    @Schema(example = "2025-04-13T15:00:00.000+00:00")
    private Date date;
}
