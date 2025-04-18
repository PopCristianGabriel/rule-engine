package com.pop.rules.repositories;

import com.pop.rules.entities.Condition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
}
