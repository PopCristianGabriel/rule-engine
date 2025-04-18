package com.pop.rules.controllers;

import com.pop.rules.entities.Condition;
import com.pop.rules.services.ConditionEvaluatorService;
import com.pop.rules.services.ConditionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rule-builder")
@RequiredArgsConstructor
public class ConditionEvaluationController {

    private final ConditionService conditionService;
    private final ConditionEvaluatorService evaluatorService;

    @PostMapping("/evaluate")
    public EvaluationResult evaluateRulesForUser(@RequestBody EvaluationRequest request) {
        List<Condition> all = conditionService.getAllConditions();

        List<Condition> passed = all.stream()
                .filter(c -> evaluatorService.evaluate(c, request.getUserId()))
                .collect(Collectors.toList());

        List<Condition> failed = all.stream()
                .filter(c -> !evaluatorService.evaluate(c, request.getUserId()))
                .collect(Collectors.toList());

        return new EvaluationResult(passed, failed);
    }

    @Data
    public static class EvaluationRequest {
        private Long userId;
    }

    @Data
    @RequiredArgsConstructor
    public static class EvaluationResult {
        private final List<Condition> passed;
        private final List<Condition> failed;
    }
}
