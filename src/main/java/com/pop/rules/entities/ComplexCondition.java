package com.pop.rules.entities;

import com.pop.rules.enums.LogicalOperator;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ComplexCondition extends Condition {

    @Enumerated(EnumType.STRING)
    private LogicalOperator operator;

    @OneToOne(cascade = CascadeType.ALL)
    private Condition left;

    @OneToOne(cascade = CascadeType.ALL)
    private Condition right;
}
