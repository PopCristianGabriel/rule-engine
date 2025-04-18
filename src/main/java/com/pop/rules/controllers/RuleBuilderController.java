package com.pop.rules.controllers;

import com.pop.rules.dtos.ConditionRequestDto;
import com.pop.rules.entities.Condition;
import com.pop.rules.services.ConditionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rule-builder")
@RequiredArgsConstructor
public class RuleBuilderController {

    private final ConditionService conditionService;

    @PostMapping("/create")
    public Condition create(@RequestBody ConditionRequestDto dto) {
        return conditionService.createFromDto(dto);
    }

    @GetMapping("/all")
    public List<Condition> getAll() {
        return conditionService.getAllConditions();
    }


}
