package com.pop.rules.entities;

import com.pop.rules.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    private Double amount;

    private Long userId;

    private Date date;
}
