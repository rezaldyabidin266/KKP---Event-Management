package com.skyline.skatecross.modules.category.mapper;

import com.skyline.skatecross.modules.category.dto.CategoryResponseDto;
import com.skyline.skatecross.modules.category.dto.EventCategoryCreateRequestDto;
import com.skyline.skatecross.modules.category.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(EventCategoryCreateRequestDto dto) {
        if (dto == null) {
            return null;
        }
        Category category = new Category();
        category.setName(dto.getName());
        category.setMinAge(dto.getMinAge());
        category.setMaxAge(dto.getMaxAge());
        category.setGender(dto.getGender().toUpperCase());
        category.setFee(dto.getFee());
        category.setQuota(dto.getQuota());
        category.setCupSize(dto.getCupSize());
        category.setPreRaceWindowMinutes(dto.getPreRaceWindowMinutes());
        category.setRegistrationOpenAt(dto.getRegistrationOpenAt());
        category.setRegistrationCloseAt(dto.getRegistrationCloseAt());
        category.setIsActive(true);
        return category;
    }

    public CategoryResponseDto toResponseDto(Category entity) {
        if (entity == null) {
            return null;
        }
        CategoryResponseDto response = new CategoryResponseDto();
        response.setId(entity.getId());
        response.setCategoryCode(entity.getCategoryCode());
        response.setName(entity.getName());
        response.setMinAge(entity.getMinAge());
        response.setMaxAge(entity.getMaxAge());
        response.setGender(entity.getGender());
        response.setFee(entity.getFee());
        response.setQuota(entity.getQuota());
        response.setCupSize(entity.getCupSize());
        response.setPreRaceWindowMinutes(entity.getPreRaceWindowMinutes());
        response.setRegistrationOpenAt(entity.getRegistrationOpenAt());
        response.setRegistrationCloseAt(entity.getRegistrationCloseAt());
        response.setIsActive(entity.getIsActive());
        return response;
    }
}