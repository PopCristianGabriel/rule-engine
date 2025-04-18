package com.pop.rules.helpers;

import com.pop.rules.dtos.ConditionRequestDto;
import com.pop.rules.entities.ComplexCondition;
import com.pop.rules.entities.Condition;
import com.pop.rules.entities.SimpleCondition;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@NoArgsConstructor
public class ConditionRequestMapper {

    public Condition toEntity(ConditionRequestDto dto) {
        if (dto.getChildren() != null && !dto.getChildren().isEmpty()) {
            ComplexCondition complex = new ComplexCondition();
            complex.setOperator(dto.getLogicalOperator());
            complex.setLeft(toEntity(dto.getChildren().get(0)));
            complex.setRight(toEntity(dto.getChildren().get(1)));
            return complex;
        }

        SimpleCondition simple = new SimpleCondition();
        simple.setEventType(dto.getEventType());
        simple.setTargetValue(dto.getTargetValue());
        simple.setOperator(dto.getOperator());
        simple.setDateRange(dto.getDateRange());
        simple.setStreakInterval(dto.getStreakInterval());
        simple.setStreakLength(dto.getStreakLength());
        simple.setRequiredDistinctIntervals(dto.getRequiredDistinctIntervals());
        return simple;
    }
}
