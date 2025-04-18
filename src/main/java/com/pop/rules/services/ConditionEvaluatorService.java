package com.pop.rules.services;

import com.pop.rules.entities.ComplexCondition;
import com.pop.rules.entities.Condition;
import com.pop.rules.entities.SimpleCondition;
import com.pop.rules.enums.EventType;
import com.pop.rules.enums.StreakInterval;
import com.pop.rules.models.ConditionContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConditionEvaluatorService {

    private final QueryResolverService queryResolver;

//    public boolean evaluate(SimpleCondition cond, Long userId) {
//
//
//        EventType eventType = switch (cond.getConditionType().toUpperCase()) {
//            case "SUM_DEPOSIT" -> EventType.DEPOSIT;
//            case "SUM_BET" -> EventType.BET;
//            default -> throw new IllegalArgumentException("Unsupported condition type: " + cond.getConditionType());
//        };
//
//        ConditionContext ctx = ConditionContext.builder()
//                .eventType(eventType)
//                .userId(userId)
//                .dateRange(cond.getDateRange())
//                .useSum(true)
//                .build();
//
//        double value = queryResolver.resolve(ctx);
//
//        return switch (cond.getOperator()) {
//            case GREATER_THAN -> value > cond.getTargetValue();
//            case GREATER_THAN_OR_EQUAL -> value >= cond.getTargetValue();
//            case LESS_THAN -> value < cond.getTargetValue();
//            case LESS_THAN_OR_EQUAL -> value <= cond.getTargetValue();
//            case EQUALS -> value == cond.getTargetValue();
//            case NOT_EQUALS -> value != cond.getTargetValue();
//            default -> throw new IllegalArgumentException("Unsupported operator: " + cond.getOperator());
//        };
//    }

    public boolean evaluate(SimpleCondition cond, Long userId) {
        ConditionContext ctx = ConditionContext.builder()
                .eventType(cond.getEventType())     // <--- just EventType now
                .userId(userId)
                .dateRange(cond.getDateRange())
                .streakInterval(cond.getStreakInterval())  // <- null if not a streak condition
                .streakLength(cond.getStreakLength())// <- null if not a streak
                .requiredDistinctIntervals(cond.getRequiredDistinctIntervals()) // <- null if not a distinct type
                .build();

        double value;

        switch (ctx.getResolutionMethod()){
            case STREAK:
                boolean satisfied = streakSatisfied(
                        queryResolver.getEventDates(ctx.getUserId(), ctx.getEventType(), ctx.getDateRange()),
                        ctx.getStreakInterval(),
                        ctx.getStreakLength()
                );
                value = satisfied ? 1.0 : 0.0;
                break;
            case SUM:
                value = queryResolver.resolve(ctx);
                break;
            case DISTINCT:
                return hasSufficientDistinctActivity(ctx, userId);
            default:
                value = queryResolver.resolve(ctx);
                break;
        }

        // 🧠 Use the operator for evaluation
        return switch (cond.getOperator()) {
            case GREATER_THAN -> value > cond.getTargetValue();
            case GREATER_THAN_OR_EQUAL -> value >= cond.getTargetValue();
            case LESS_THAN -> value < cond.getTargetValue();
            case LESS_THAN_OR_EQUAL -> value <= cond.getTargetValue();
            case EQUALS -> value == cond.getTargetValue();
            case NOT_EQUALS -> value != cond.getTargetValue();
            default -> throw new IllegalArgumentException("Unsupported operator: " + cond.getOperator());
        };
    }


    public boolean evaluate(ComplexCondition cond, Long userId) {
        boolean left = evaluate(cond.getLeft(), userId);
        boolean right = evaluate(cond.getRight(), userId);

        return switch (cond.getOperator()) {
            case AND -> left && right;
            case OR -> right || left;
            default -> throw new IllegalArgumentException("Unsupported logical operator: " + cond.getOperator());
        };
    }

    public boolean evaluate(Condition cond, Long userId) {
        if (cond instanceof SimpleCondition s) {
            return evaluate(s, userId);
        } else if (cond instanceof ComplexCondition c) {
            return evaluate(c, userId);
        } else {
            throw new IllegalArgumentException("Unknown condition type: " + cond.getClass());
        }
    }

    private boolean streakSatisfied(List<Date> eventDates, StreakInterval interval, int streakLength) {
        if (eventDates.isEmpty()) return false;

        // Convert to LocalDate and sort
        Set<LocalDate> uniqueDates = eventDates.stream()
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .collect(Collectors.toSet());

        List<LocalDate> sortedDates = uniqueDates.stream()
                .sorted()
                .toList();

        for (int i = 0; i <= sortedDates.size() - streakLength; i++) {
            boolean isStreak = true;
            for (int j = 0; j < streakLength; j++) {
                LocalDate expected = switch (interval) {
                    case DAILY -> sortedDates.get(i).plusDays(j);
                    case WEEKLY -> sortedDates.get(i).plusWeeks(j);
                    case MONTHLY -> sortedDates.get(i).plusMonths(j);
                };
                if (!uniqueDates.contains(expected)) {
                    isStreak = false;
                    break;
                }
            }
            if (isStreak) return true;
        }

        return false;
    }

    private boolean hasSufficientDistinctActivity(ConditionContext cond, Long userId) {
        List<Date> eventDates = queryResolver.getEventDates(userId, cond.getEventType(), cond.getDateRange());

        Set<String> distinctIntervals = eventDates.stream()
                .map(date -> {
                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return switch (cond.getStreakInterval()) {
                        case DAILY -> localDate.toString(); // yyyy-MM-dd
                        case WEEKLY -> localDate.getYear() + "-W" + localDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
                        case MONTHLY -> localDate.getYear() + "-" + localDate.getMonthValue();
                    };
                })
                .collect(Collectors.toSet());

        return distinctIntervals.size() >= cond.getRequiredDistinctIntervals();
    }

}
