package com.pop.rules.dtos;

import com.pop.rules.enums.LogicalOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ComplexConditionDto extends ConditionDto {
    private LogicalOperator operator;
    private ConditionDto left;
    private ConditionDto right;
}

