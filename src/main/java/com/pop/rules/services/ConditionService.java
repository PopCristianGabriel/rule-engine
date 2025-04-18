package com.pop.rules.services;

import com.pop.rules.dtos.ConditionRequestDto;
import com.pop.rules.entities.Condition;
import com.pop.rules.helpers.ConditionRequestMapper;
import com.pop.rules.repositories.ConditionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConditionService {

    private final ConditionRepository conditionRepository;
    private final ConditionRequestMapper conditionRequestMapper;


    public Condition createFromDto(ConditionRequestDto dto) {
        Condition condition = conditionRequestMapper.toEntity(dto);
        return conditionRepository.save(condition);
    }

    public List<Condition> getAllConditions() {
        return conditionRepository.findAll();
    }
}
