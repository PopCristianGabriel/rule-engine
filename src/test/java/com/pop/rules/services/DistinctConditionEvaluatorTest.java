package com.pop.rules.services;

import com.pop.rules.entities.Event;
import com.pop.rules.entities.SimpleCondition;
import com.pop.rules.enums.DateRange;
import com.pop.rules.enums.EventType;
import com.pop.rules.enums.LogicalOperator;
import com.pop.rules.enums.StreakInterval;
import com.pop.rules.repositories.EventRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DistinctConditionEvaluatorTest {

    @Autowired
    private ConditionEvaluatorService evaluatorService;

    @Autowired
    private EventRepository eventRepository;

    private final Long userId = 300L;

    @BeforeEach
    void setup() {
        eventRepository.deleteAll();
    }

    private void saveBetEventsOnDays(int... days) {
        for (int d : days) {
            Date date = Date.from(LocalDate.now()
                    .withDayOfMonth(1)
                    .plusDays(d)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant());

            eventRepository.save(new Event(null, EventType.BET, 1.0, userId, date));
        }
    }

    private SimpleCondition buildDistinctCondition(int requiredDays) {
        SimpleCondition condition = new SimpleCondition();
        condition.setEventType(EventType.BET);
        condition.setOperator(LogicalOperator.GREATER_THAN_OR_EQUAL);
        condition.setTargetValue(1.0); // Will always be compared against 1.0
        condition.setDateRange(DateRange.THIS_MONTH);
        condition.setStreakInterval(StreakInterval.DAILY); // Defines grouping type
        condition.setRequiredDistinctIntervals(requiredDays);
        return condition;
    }

    @Test
    void shouldReturnTrue_whenUserHasEventsOnEnoughDistinctDays() {
        saveBetEventsOnDays(0, 2, 4, 6, 8); // 5 unique days

        SimpleCondition condition = buildDistinctCondition(5);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalse_whenUserHasEventsOnFewerDaysThanRequired() {
        saveBetEventsOnDays(1, 2, 4); // 3 unique days

        SimpleCondition condition = buildDistinctCondition(5);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertFalse(result);
    }

    @Test
    void shouldReturnTrue_whenUserHasMultipleEventsOnSameDaysButCountsOnce() {
        saveBetEventsOnDays(1, 1, 2, 2, 3, 3, 4, 4, 5); // Only 5 distinct days

        SimpleCondition condition = buildDistinctCondition(5);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalse_whenNoEventsExist() {
        SimpleCondition condition = buildDistinctCondition(3);
        boolean result = evaluatorService.evaluate(condition, userId);
        assertFalse(result);
    }

    @Test
    void shouldReturnTrue_whenDistinctWeeksMatch() {
        // For WEEKLY interval: events across 3 separate weeks
        LocalDate start = LocalDate.now().withDayOfMonth(1);
        eventRepository.save(new Event(null, EventType.BET, 1.0, userId, Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant())));
        eventRepository.save(new Event(null, EventType.BET, 1.0, userId, Date.from(start.plusWeeks(1).atStartOfDay(ZoneId.systemDefault()).toInstant())));
        eventRepository.save(new Event(null, EventType.BET, 1.0, userId, Date.from(start.plusWeeks(2).atStartOfDay(ZoneId.systemDefault()).toInstant())));
        var dawd = eventRepository.findAll();
        SimpleCondition condition = new SimpleCondition();
        condition.setEventType(EventType.BET);
        condition.setOperator(LogicalOperator.GREATER_THAN_OR_EQUAL);
        condition.setTargetValue(1.0);
        condition.setDateRange(DateRange.THIS_MONTH);
        condition.setStreakInterval(StreakInterval.WEEKLY);
        condition.setRequiredDistinctIntervals(3);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertTrue(result);
    }
}
