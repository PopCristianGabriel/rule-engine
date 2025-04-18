package com.pop.rules.dtos;

import com.pop.rules.enums.DateRange;
import com.pop.rules.enums.EventType;
import com.pop.rules.enums.LogicalOperator;
import com.pop.rules.enums.StreakInterval;
import lombok.Data;

import java.util.List;

@Data
public class ConditionRequestDto {
    private EventType eventType;
    private LogicalOperator operator;
    private Double targetValue;
    private DateRange dateRange;
    private StreakInterval streakInterval;
    private Integer streakLength;
    private Integer requiredDistinctIntervals;

    // Complex support
    private List<ConditionRequestDto> children;
    private LogicalOperator logicalOperator; // only used for complex conditions
}