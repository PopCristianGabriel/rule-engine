package com.pop.rules.services;

import com.pop.rules.entities.Event;
import com.pop.rules.enums.DateRange;
import com.pop.rules.enums.EventType;
import com.pop.rules.models.ConditionContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryResolverService {

    private final EntityManager entityManager;

    public Double resolve(ConditionContext ctx) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Double> query = cb.createQuery(Double.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("userId"), ctx.getUserId()));

        if (ctx.getEventType() != null) {
            predicates.add(cb.equal(root.get("eventType"), ctx.getEventType()));
        }

        if (ctx.getDateRange() != null) {
            LocalDateTime now = LocalDateTime.now();
            Date start = null, end = null;

            switch (ctx.getDateRange()) {
                case TODAY -> {
                    start = Date.from(now.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    end = Date.from(now.toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
                }
                case THIS_MONTH -> {
                    start = Date.from(now.toLocalDate().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
                    end = Date.from(now.toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
                }
            }

            predicates.add(cb.between(root.get("date"), start, end));
        }

        if (ctx.isUseSum()) {
            query.select(cb.coalesce(cb.sum(root.get("amount")).as(Double.class), 0D));
        } else {
            query.select(cb.literal(1.0)); // neutral fallback — can be replaced with count, etc.
        }

        query.where(cb.and(predicates.toArray(new Predicate[0])));

        return entityManager.createQuery(query).getSingleResult();
    }

    public List<Date> getEventDates(Long userId, EventType eventType, DateRange dateRange) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Date> query = cb.createQuery(Date.class);
        Root<Event> root = query.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        // Required filters
        predicates.add(cb.equal(root.get("userId"), userId));
        predicates.add(cb.equal(root.get("eventType"), eventType));

        // Optional date range filtering
        if (dateRange != null) {
            LocalDateTime now = LocalDateTime.now();
            Date start = null;
            Date end = null;

            switch (dateRange) {
                case TODAY -> {
                    start = Date.from(now.toLocalDate()
                            .atStartOfDay(ZoneId.systemDefault())
                            .toInstant());
                    end = Date.from(now.toLocalDate()
                            .atTime(LocalTime.MAX)
                            .atZone(ZoneId.systemDefault())
                            .toInstant());
                }
                case THIS_MONTH -> {
                    LocalDate firstDay = now.toLocalDate().withDayOfMonth(1);
                    LocalDate lastDay = now.toLocalDate().withDayOfMonth(now.toLocalDate().lengthOfMonth());

                    start = Date.from(firstDay.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    end = Date.from(lastDay.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
                }
            }

            predicates.add(cb.between(root.get("date"), start, end));
        }

        query.select(root.get("date"))
                .where(cb.and(predicates.toArray(new Predicate[0])))
                .orderBy(cb.asc(root.get("date"))); // optional: sorted for streaks

        return entityManager.createQuery(query).getResultList();
    }

}
