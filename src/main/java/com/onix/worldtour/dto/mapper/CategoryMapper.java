package com.onix.worldtour.dto.mapper;

import com.onix.worldtour.controller.request.CategoryRequest;
import com.onix.worldtour.dto.model.CategoryDto;
import com.onix.worldtour.model.Category;

public class CategoryMapper {
    public static Category toCategory(CategoryRequest categoryRequest) {
        return new Category()
                .setName(categoryRequest.getName())
                .setLevel(categoryRequest.getLevel())
                .setDescription(categoryRequest.getDescription());
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto()
                .setId(category.getId())
                .setName(category.getName())
                .setLevel(category.getLevel())
                .setDescription(category.getDescription());
    }

    public static CategoryDto toCategoryOptionDto(Category category) {
        return new CategoryDto()
                .setId(category.getId())
                .setName(category.getName());
    }
}