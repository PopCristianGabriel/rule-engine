package com.pop.rules.dtos;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleConditionDto.class, name = "SIMPLE"),
        @JsonSubTypes.Type(value = ComplexConditionDto.class, name = "COMPLEX")
})
@Data
public abstract class ConditionDto {
    private Long id;
    private String type; // "SIMPLE" or "COMPLEX"
}
