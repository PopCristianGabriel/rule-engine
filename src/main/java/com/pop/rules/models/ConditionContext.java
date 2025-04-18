package com.pop.rules.models;

import com.pop.rules.enums.DateRange;
import com.pop.rules.enums.EventType;
import com.pop.rules.enums.ResolutionMethod;
import com.pop.rules.enums.StreakInterval;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Builder
@Data
public class ConditionContext {
    @NonNull
    private Long userId;

    private EventType eventType;     // optional
    private DateRange dateRange;     // optional

    public boolean isUseSum() {
        return streakInterval == null && streakLength == null;
    }

    @Enumerated(EnumType.STRING)
    private StreakInterval streakInterval;

    private Integer streakLength;

    private Integer requiredDistinctIntervals;

    public ResolutionMethod getResolutionMethod() {
        if (requiredDistinctIntervals != null) {
            return ResolutionMethod.DISTINCT;
        }
        if (getStreakInterval() != null && getStreakLength() != null) {
            return ResolutionMethod.STREAK;
        }
        return ResolutionMethod.SUM;
    }
}
