package com.skyline.skatecross.modules.event.mapper;


import com.skyline.skatecross.modules.category.dto.CategoryResponseDto;
import com.skyline.skatecross.modules.category.mapper.CategoryMapper;
import com.skyline.skatecross.modules.event.dto.EventCreateRequestDto;
import com.skyline.skatecross.modules.event.dto.EventResponseDto;
import com.skyline.skatecross.modules.event.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    @Autowired
    private CategoryMapper categoryMapper;

    public Event toEntity(EventCreateRequestDto dto) {
        if (dto == null) return null;

        Event event = new Event();
        event.setName(dto.getName());
        event.setEventDate(dto.getEventDate());
        event.setRegistrationOpenAt(dto.getRegistrationOpenAt());
        event.setRegistrationCloseAt(dto.getRegistrationCloseAt());
        event.setCreatedByUserId(dto.getPicUserId());
        event.setStatus("DRAFT");
        event.setNextRegistrationNo(1L);
        return event;
    }

    public EventResponseDto toResponseDto(Event entity) {
        if (entity == null) return null;

        EventResponseDto response = new EventResponseDto();
        response.setId(entity.getId());
        response.setEventCode(entity.getEventCode());
        response.setName(entity.getName());
        response.setEventDate(entity.getEventDate());
        response.setRegistrationOpenAt(entity.getRegistrationOpenAt());
        response.setRegistrationCloseAt(entity.getRegistrationCloseAt());
        response.setStatus(entity.getStatus());
        response.setCreatedByUserId(entity.getCreatedByUserId());
        response.setNextRegistrationNo(entity.getNextRegistrationNo());

        if (entity.getCategories() != null) {
            List<CategoryResponseDto> categoryDtos = entity.getCategories()
                    .stream()
                    .map(categoryMapper::toResponseDto)
                    .collect(Collectors.toList());
            response.setCategories(categoryDtos);
        }

        return response;
    }
}