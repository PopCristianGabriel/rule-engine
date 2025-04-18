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
public class ComplexNestedConditionTest {

    @Autowired
    private ConditionEvaluatorService evaluatorService;

    @Autowired
    private EventRepository eventRepository;

    private final Long userId = 999L;

    private SimpleCondition depositAtLeast1;
    private SimpleCondition depositLessThan10;
    private SimpleCondition betAtLeast2;
    private ComplexCondition andBlock;
    private ComplexCondition root;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();

        // Common condition setup
        depositAtLeast1 = new SimpleCondition();
        depositAtLeast1.setEventType(EventType.DEPOSIT);
        depositAtLeast1.setOperator(LogicalOperator.GREATER_THAN_OR_EQUAL);
        depositAtLeast1.setTargetValue(1.0);

        depositLessThan10 = new SimpleCondition();
        depositLessThan10.setEventType(EventType.DEPOSIT);
        depositLessThan10.setOperator(LogicalOperator.LESS_THAN);
        depositLessThan10.setTargetValue(10.0);

        betAtLeast2 = new SimpleCondition();
        betAtLeast2.setEventType(EventType.BET);
        betAtLeast2.setOperator(LogicalOperator.GREATER_THAN_OR_EQUAL);
        betAtLeast2.setTargetValue(2.0);

        andBlock = new ComplexCondition();
        andBlock.setLeft(depositAtLeast1);
        andBlock.setRight(depositLessThan10);
        andBlock.setOperator(LogicalOperator.AND);

        root = new ComplexCondition();
        root.setLeft(andBlock);
        root.setRight(betAtLeast2);
        root.setOperator(LogicalOperator.OR);
    }

    @Test
    void shouldEvaluateNestedCondition_whenAndIsNestedInsideOr() {
        // 2 deposits
        eventRepository.save(new Event(null, EventType.DEPOSIT, 1.0, userId, new Date()));
        eventRepository.save(new Event(null, EventType.DEPOSIT, 3.0, userId, new Date()));
        // 2 bets
        eventRepository.save(new Event(null, EventType.BET, 2.0, userId, new Date()));
        eventRepository.save(new Event(null, EventType.BET, 1.0, userId, new Date()));

        boolean result = evaluatorService.evaluate(root, userId);
        assertTrue(result);
    }

    @Test
    void shouldFail_whenNoEventsExist() {
        boolean result = evaluatorService.evaluate(root, userId);
        assertFalse(result);
    }

    @Test
    void shouldFail_whenDepositsAreTooLow_andBetIsMissing() {
        eventRepository.save(new Event(null, EventType.DEPOSIT, 0.0, userId, new Date()));
        boolean result = evaluatorService.evaluate(root, userId);
        assertFalse(result);
    }

    @Test
    void shouldFail_whenDepositOnlyMatchesOneSide_ofAndBlock_andNoBets() {
        eventRepository.save(new Event(null, EventType.DEPOSIT, 20.0, userId, new Date())); // breaks < 10
        boolean result = evaluatorService.evaluate(root, userId);
        assertFalse(result);
    }

    @Test
    void shouldFail_whenBetCountIsNotEnough_andDepositFails() {
        eventRepository.save(new Event(null, EventType.BET, 1.0, userId, new Date())); // only 1 bet
        eventRepository.save(new Event(null, EventType.BET, 0.5, userId, new Date())); // only 1 bet
        boolean result = evaluatorService.evaluate(root, userId);
        assertFalse(result);
    }

    @Test
    void shouldPass_whenOnlyBetSideMatches_andDepositFails() {
        eventRepository.save(new Event(null, EventType.BET, 1.0, userId, new Date()));
        eventRepository.save(new Event(null, EventType.BET, 1.0, userId, new Date()));
        boolean result = evaluatorService.evaluate(root, userId);
        assertTrue(result); // right side saves it
    }
}
