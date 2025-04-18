package com.pop.rules.helpers;

import com.pop.rules.dtos.EventDto;
import com.pop.rules.entities.Event;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MapperHelper {

    private final ModelMapper modelMapper;

    public <D, T> D mapToDto(T entity, Class<D> outClass) {
        return modelMapper.map(entity, outClass);
    }

    public <D, T> T mapToEntity(D dto, Class<T> entityClass) {
        return modelMapper.map(dto, entityClass);
    }

    @PostConstruct
    public void init() {
        modelMapper.typeMap(EventDto.class, Event.class)
                .addMappings(mapper -> mapper.skip(Event::setId));
    }
}
