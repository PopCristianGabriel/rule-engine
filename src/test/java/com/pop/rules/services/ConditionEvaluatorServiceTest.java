package com.pop.rules.services;

import com.pop.rules.entities.ComplexCondition;
import com.pop.rules.entities.Event;
import com.pop.rules.entities.SimpleCondition;
import com.pop.rules.enums.EventType;
import com.pop.rules.enums.LogicalOperator;
import com.pop.rules.repositories.EventRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ConditionEvaluatorServiceTest {

    @Autowired
    private ConditionEvaluatorService evaluatorService;

    @Autowired
    private EventRepository eventRepository;

    private final Long userId = 100L;


    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();

        eventRepository.save(new Event(null, EventType.DEPOSIT, 1.0, userId, new Date()));
        eventRepository.save(new Event(null, EventType.DEPOSIT, 2.0, userId, new Date()));
        eventRepository.save(new Event(null, EventType.DEPOSIT, 3.0, userId, new Date()));
    }

    @Test
    void shouldReturnFalse_whenUserHasDepositedLessThan10() {
        SimpleCondition condition = new SimpleCondition();
        condition.setEventType(EventType.DEPOSIT);
        condition.setOperator(LogicalOperator.GREATER_THAN_OR_EQUAL);
        condition.setTargetValue(10.0);
        condition.setDateRange(null);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertFalse(result);
    }

    @Test
    void shouldReturnTrue_whenUserHasDepositedAtLeast5() {
        SimpleCondition condition = new SimpleCondition();
        condition.setEventType(EventType.DEPOSIT);
        condition.setOperator(LogicalOperator.GREATER_THAN_OR_EQUAL);
        condition.setTargetValue(5.0);
        condition.setDateRange(null);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertTrue(result);
    }

    @Test
    void complexCondition_shouldEvaluateTrue_whenBothSimpleConditionsAreTrue() {
        // SimpleCondition 1: SUM_DEPOSIT >= 5
        SimpleCondition cond1 = new SimpleCondition();
        cond1.setEventType(EventType.DEPOSIT);
        cond1.setOperator(LogicalOperator.GREATER_THAN_OR_EQUAL);
        cond1.setTargetValue(5.0);
        cond1.setDateRange(null);

        // SimpleCondition 2: SUM_DEPOSIT <= 10
        SimpleCondition cond2 = new SimpleCondition();
        cond2.setEventType(EventType.DEPOSIT);
        cond2.setOperator(LogicalOperator.LESS_THAN_OR_EQUAL);
        cond2.setTargetValue(10.0);
        cond2.setDateRange(null);

        // ComplexCondition: cond1 AND cond2
        ComplexCondition complex = new ComplexCondition();
        complex.setLeft(cond1);
        complex.setRight(cond2);
        complex.setOperator(LogicalOperator.AND);

        boolean result = evaluatorService.evaluate(complex, userId);
        assertTrue(result);
    }

    @Test
    void complexCondition_shouldEvaluateFalse_whenOneConditionFails() {
        // SimpleCondition 1: SUM_DEPOSIT >= 10 → ❌
        SimpleCondition cond1 = new SimpleCondition();
        cond1.setEventType(EventType.DEPOSIT);
        cond1.setOperator(LogicalOperator.GREATER_THAN_OR_EQUAL);
        cond1.setTargetValue(10.0);
        cond1.setDateRange(null);

        // SimpleCondition 2: SUM_DEPOSIT >= 2 → ✅
        SimpleCondition cond2 = new SimpleCondition();
        cond2.setEventType(EventType.DEPOSIT);
        cond2.setOperator(LogicalOperator.GREATER_THAN_OR_EQUAL);
        cond2.setTargetValue(2.0);
        cond2.setDateRange(null);

        // ComplexCondition: cond1 AND cond2 → ❌
        ComplexCondition complex = new ComplexCondition();
        complex.setLeft(cond1);
        complex.setRight(cond2);
        complex.setOperator(LogicalOperator.AND);

        boolean result = evaluatorService.evaluate(complex, userId);
        assertFalse(result);
    }
}
