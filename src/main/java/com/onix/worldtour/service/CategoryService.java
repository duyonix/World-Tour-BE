package com.onix.worldtour.service;

import com.onix.worldtour.controller.request.CategoryRequest;
import com.onix.worldtour.dto.mapper.CategoryMapper;
import com.onix.worldtour.dto.model.CategoryDto;
import com.onix.worldtour.exception.ApplicationException;
import com.onix.worldtour.exception.EntityType;
import com.onix.worldtour.exception.ExceptionType;
import com.onix.worldtour.model.Category;
import com.onix.worldtour.model.Region;
import com.onix.worldtour.repository.CategoryRepository;
import com.onix.worldtour.util.ValueMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryDto addCategory(CategoryRequest categoryRequest) {
        log.info("CategoryService::addCategory execution started");
        CategoryDto categoryDto;

        Optional<Category> category = categoryRepository.findByName(categoryRequest.getName());
        if (category.isPresent()) {
            log.error("CategoryService::addCategory execution failed with duplicate category name {}", categoryRequest.getName());
            throw exception(EntityType.CATEGORY, ExceptionType.DUPLICATE_ENTITY, categoryRequest.getName());
        }

        category = categoryRepository.findByLevel(categoryRequest.getLevel());
        if (category.isPresent()) {
            log.error("CategoryService::addCategory execution failed with duplicate category level {}", categoryRequest.getLevel());
            throw exception(EntityType.CATEGORY, ExceptionType.DUPLICATE_LEVEL, categoryRequest.getLevel().toString());
        }

        try {
            Category newCategory = CategoryMapper.toCategory(categoryRequest);
            log.debug("CategoryService::addCategory request parameters {}", ValueMapper.jsonAsString(newCategory));

            Category savedCategory = categoryRepository.save(newCategory);
            categoryDto = CategoryMapper.toCategoryDto(savedCategory);
            log.debug("CategoryService::addCategory received response from database {}", ValueMapper.jsonAsString(categoryDto));

        } catch (Exception e) {
            log.error("CategoryService::addCategory execution failed with error {}", e.getMessage());
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("CategoryService::addCategory execution completed");
        return categoryDto;
    }

    public Page<CategoryDto> getCategories(Integer page, Integer size, String search) {
        log.info("CategoryService::getCategories execution started");
        Page<CategoryDto> categoryDtos;
        try {
            log.debug("CategoryService::getCategories request parameters page {}, size {}, search {}", page, size, search);
            Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
            Page<Category> categories = categoryRepository.findByNameContainingIgnoreCase(search, pageable);

            categoryDtos = categories.map(CategoryMapper::toCategoryDto);
            log.debug("CategoryService::getCategories received response from database {}", ValueMapper.jsonAsString(categoryDtos));
        } catch (Exception e) {
            log.error("CategoryService::getCategories execution failed with error {}", e.getMessage());
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("CategoryService::getCategories execution completed");
        return categoryDtos;
    }

    public List<CategoryDto> getCategoryOptions() {
        log.info("CategoryService::getCategoryOptions execution started");
        List<CategoryDto> categoryDtos;
        try {
            List<Category> categories = categoryRepository.findAll();

            categoryDtos = categories.stream().map(CategoryMapper::toCategoryOptionDto).toList();
            log.debug("CategoryService::getCategoryOptions received response from database {}", ValueMapper.jsonAsString(categoryDtos));
        } catch (Exception e) {
            log.error("CategoryService::getCategoryOptions execution failed with error {}", e.getMessage());
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("CategoryService::getCategoryOptions execution completed");
        return categoryDtos;
    }

    public CategoryDto getCategory(Integer id) {
        log.info("CategoryService::getCategory execution started");
        CategoryDto categoryDto;

        log.debug("CategoryService::getCategory request parameters id {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            log.error("CategoryService::getCategory execution failed with category not found {}", id);
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_NOT_FOUND, id.toString());
        }

        categoryDto = CategoryMapper.toCategoryDto(category.get());
        log.debug("CategoryService::getCategory received response from database {}", ValueMapper.jsonAsString(categoryDto));

        log.info("CategoryService::getCategory execution completed");
        return categoryDto;
    }

    public CategoryDto updateCategory(Integer id, CategoryRequest categoryRequest) {
        log.info("CategoryService::updateCategory execution started");
        CategoryDto categoryDto;

        log.debug("CategoryService::updateCategory request parameters id {}, categoryRequest {}", id, ValueMapper.jsonAsString(categoryRequest));
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            log.error("CategoryService::updateCategory execution failed with category not found {}", id);
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_NOT_FOUND, id.toString());
        }

        Optional<Category> duplicateCategory = categoryRepository.findByName(categoryRequest.getName());
        if (duplicateCategory.isPresent() && !duplicateCategory.get().getId().equals(category.get().getId())) {
            log.error("CategoryService::updateCategory execution failed with duplicate category name {}", categoryRequest.getName());
            throw exception(EntityType.CATEGORY, ExceptionType.DUPLICATE_ENTITY, categoryRequest.getName());
        }

        duplicateCategory = categoryRepository.findByLevel(categoryRequest.getLevel());
        if (duplicateCategory.isPresent() && !duplicateCategory.get().getId().equals(category.get().getId())) {
            log.error("CategoryService::updateCategory execution failed with duplicate category level {}", categoryRequest.getLevel());
            throw exception(EntityType.CATEGORY, ExceptionType.DUPLICATE_LEVEL, categoryRequest.getLevel().toString());
        }

        try {
            Category updatedCategory = CategoryMapper.toCategory(categoryRequest);
            updatedCategory.setId(category.get().getId())
                           .setRegions(category.get().getRegions());
            log.debug("CategoryService::updateCategory request parameters {}", ValueMapper.jsonAsString(updatedCategory));

            Category savedCategory = categoryRepository.save(updatedCategory);
            categoryDto = CategoryMapper.toCategoryDto(savedCategory);
            log.debug("CategoryService::updateCategory received response from database {}", ValueMapper.jsonAsString(categoryDto));
        } catch (Exception e) {
            log.error("CategoryService::updateCategory execution failed with error {}", e.getMessage());
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("CategoryService::updateCategory execution completed");
        return categoryDto;
    }

    public CategoryDto deleteCategory(Integer id) {
        log.info("CategoryService::deleteCategory execution started");
        CategoryDto categoryDto;

        log.debug("CategoryService::deleteCategory request parameters id {}", id);
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            log.error("CategoryService::deleteCategory execution failed with category not found {}", id);
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_NOT_FOUND, id.toString());
        }

        List<Region> regions = category.get().getRegions();
        if (regions != null && !regions.isEmpty()) {
            log.error("CategoryService::deleteCategory execution failed with category in use {}", id);
            throw exception(EntityType.CATEGORY, ExceptionType.ALREADY_USED_ELSEWHERE, id.toString());
        }

        try {
            categoryDto = CategoryMapper.toCategoryDto(category.get());
            categoryRepository.delete(category.get());
            log.debug("CategoryService::deleteCategory received response from database {}", ValueMapper.jsonAsString(categoryDto));
        } catch (Exception e) {
            log.error("CategoryService::deleteCategory execution failed with error {}", e.getMessage());
            throw exception(EntityType.CATEGORY, ExceptionType.ENTITY_EXCEPTION, e.getMessage());
        }

        log.info("CategoryService::deleteCategory execution completed");
        return categoryDto;
    }

    private RuntimeException exception(EntityType entityType, ExceptionType exceptionType, String... args) {
        return ApplicationException.throwException(entityType, exceptionType, args);
    }
}