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
public class StreakConditionEvaluatorTest {

    @Autowired
    private ConditionEvaluatorService evaluatorService;

    @Autowired
    private EventRepository eventRepository;

    private final Long userId = 200L;

    @BeforeEach
    void cleanSlate() {
        eventRepository.deleteAll();
    }

    private void saveBetEventsOnDays(int... dayOffsets) {
        for (int offset : dayOffsets) {
            Date date = Date.from(LocalDate.now()
                    .withDayOfMonth(1)
                    .plusDays(offset)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant());

            eventRepository.save(new Event(null, EventType.BET, 1.0, userId, date));
        }
    }

    private SimpleCondition buildStreakCondition(int streakLength) {
        SimpleCondition condition = new SimpleCondition();
        condition.setEventType(EventType.BET);
        condition.setOperator(LogicalOperator.EQUALS);
        condition.setTargetValue(1.0);
        condition.setDateRange(DateRange.THIS_MONTH);
        condition.setStreakInterval(StreakInterval.DAILY);
        condition.setStreakLength(streakLength);
        return condition;
    }

    @Test
    void shouldReturnTrue_whenUserHas7DayDailyStreak() {
        saveBetEventsOnDays(0, 1, 2, 3, 4, 5, 6); // 7 consecutive days

        SimpleCondition condition = buildStreakCondition(7);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalse_whenStreakIsBrokenInMiddle() {
        saveBetEventsOnDays(0, 1, 2, 4, 5, 6, 7); // missing day 3

        SimpleCondition condition = buildStreakCondition(7);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertFalse(result);
    }

    @Test
    void shouldReturnTrue_whenStreakIsPartOfLongerRange() {
        saveBetEventsOnDays(1, 2, 3, 4, 5, 6, 7, 8, 9); // 9 days, valid streak inside

        SimpleCondition condition = buildStreakCondition(7);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertTrue(result);
    }

    @Test
    void shouldReturnFalse_whenNotEnoughEvents() {
        saveBetEventsOnDays(0, 1, 2, 3, 4, 5); // only 6 days

        SimpleCondition condition = buildStreakCondition(7);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertFalse(result);
    }

    @Test
    void shouldReturnFalse_whenEventsAreNonConsecutive() {
        saveBetEventsOnDays(0, 2, 4, 6, 8, 10, 12); // scattered

        SimpleCondition condition = buildStreakCondition(7);

        boolean result = evaluatorService.evaluate(condition, userId);
        assertFalse(result);
    }
}
