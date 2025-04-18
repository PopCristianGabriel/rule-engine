package com.pop.rules.dtos;

import com.pop.rules.enums.DateRange;
import com.pop.rules.enums.LogicalOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SimpleConditionDto extends ConditionDto {
    private String conditionType;
    private DateRange dateRange;
    private LogicalOperator operator;
    private Double targetValue;
}
