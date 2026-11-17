package com.skyline.skatecross.modules.event.service;

import com.skyline.skatecross.modules.event.dto.EventCreateRequestDto;
import com.skyline.skatecross.modules.event.dto.EventResponseDto;

import java.util.List;

public interface EventService {

    EventResponseDto createEvent(EventCreateRequestDto request);

    List<EventResponseDto> getAllEvents();

    EventResponseDto getEventById(Long id);
}