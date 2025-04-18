package com.pop.rules.controllers;

import com.pop.rules.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RuleBuilderMetadataController {

    @GetMapping("/rule-builder/metadata")
    public RuleBuilderMetadata getMetadata() {
        return new RuleBuilderMetadata(
                List.of(EventType.values()),
                List.of(DateRange.values()),
                List.of(LogicalOperator.values()),
                List.of(StreakInterval.values())
        );
    }

    @Data
    @AllArgsConstructor
    public static class RuleBuilderMetadata {
        private List<EventType> eventTypes;
        private List<DateRange> dateRanges;
        private List<LogicalOperator> operators;
        private List<StreakInterval> streakIntervals;
    }
}
