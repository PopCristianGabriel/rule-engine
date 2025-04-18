package com.pop.rules.services;

import com.pop.rules.dtos.EventDto;
import com.pop.rules.entities.Event;
import com.pop.rules.helpers.MapperHelper;
import com.pop.rules.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final MapperHelper mapperHelper;

    public List<EventDto> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(event -> mapperHelper.mapToDto(event, EventDto.class))
                .collect(Collectors.toList());
    }

    public Optional<EventDto> getEventById(Long id) {
        return eventRepository.findById(id)
                .map(event -> mapperHelper.mapToDto(event, EventDto.class));
    }

    public EventDto createEvent(EventDto eventDto) {
        Event event = mapperHelper.mapToEntity(eventDto, Event.class);
        Event saved = eventRepository.save(event);
        return mapperHelper.mapToDto(saved, EventDto.class);
    }

    public EventDto updateEvent(Long id, EventDto updatedDto) {
        return eventRepository.findById(id)
                .map(existing -> {
                    Event updated = mapperHelper.mapToEntity(updatedDto, Event.class);
                    updated.setId(existing.getId()); // ensure correct ID
                    Event saved = eventRepository.save(updated);
                    return mapperHelper.mapToDto(saved, EventDto.class);
                })
                .orElseThrow(() -> new RuntimeException("Event not found with id " + id));
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public List<EventDto> createEvents(List<EventDto> eventDtos) {
        List<Event> events = eventDtos.stream()
                .map(dto -> mapperHelper.mapToEntity(dto, Event.class))
                .toList();

        List<Event> saved = eventRepository.saveAll(events);

        return saved.stream()
                .map(event -> mapperHelper.mapToDto(event, EventDto.class))
                .toList();
    }

}
