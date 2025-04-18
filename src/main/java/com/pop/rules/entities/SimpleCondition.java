package com.pop.rules.entities;

import com.pop.rules.enums.DateRange;
import com.pop.rules.enums.EventType;
import com.pop.rules.enums.LogicalOperator;
import com.pop.rules.enums.StreakInterval;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SimpleCondition extends Condition {

    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Enumerated(EnumType.STRING)
    private StreakInterval streakInterval;

    private Integer streakLength;

    @Enumerated(EnumType.STRING)
    private LogicalOperator operator;

    private Double targetValue;

    @Enumerated(EnumType.STRING)
    private DateRange dateRange;

    private Integer requiredDistinctIntervals;

}
