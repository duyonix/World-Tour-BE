package com.onix.worldtour.controller.api;

import com.onix.worldtour.controller.request.CategoryRequest;
import com.onix.worldtour.dto.model.CategoryDto;
import com.onix.worldtour.dto.response.Response;
import com.onix.worldtour.service.CategoryService;
import com.onix.worldtour.util.ValueMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Response> addCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        log.info("CategoryController::addCategory request body {}", ValueMapper.jsonAsString(categoryRequest));
        CategoryDto categoryDto = categoryService.addCategory(categoryRequest);

        Response<Object> response = Response.ok().setPayload(categoryDto);
        log.info("CategoryController::addCategory response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Response> getCategories(
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "search", defaultValue = "") String search
    ) {
        log.info("CategoryController::getCategories page {} size {} search {}", page, size, search);
        Page<CategoryDto> categories = categoryService.getCategories(page, size, search);
        Response<Object> response = Response.ok().setPayload(categories);
        log.info("CategoryController::getCategories response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getCategory(@PathVariable("id") Integer id) {
        log.info("CategoryController::getCategory by id {}", id);
        CategoryDto category = categoryService.getCategory(id);
        Response<Object> response = Response.ok().setPayload(category);
        log.info("CategoryController::getCategory by id {} response {}", id, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/options")
    public ResponseEntity<Response> getCategoryOptions() {
        log.info("CategoryController::getCategoryOptions");
        List<CategoryDto> categories = categoryService.getCategoryOptions();
        Response<Object> response = Response.ok().setPayload(categories);
        log.info("CategoryController::getCategoryOptions response {}", ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Response> updateCategory(@PathVariable("id") Integer id, @RequestBody @Valid CategoryRequest categoryRequest) {
        log.info("CategoryController::updateCategory by id {} request body {}", id, ValueMapper.jsonAsString(categoryRequest));
        CategoryDto categoryDto = categoryService.updateCategory(id, categoryRequest);

        Response<Object> response = Response.ok().setPayload(categoryDto);
        log.info("CategoryController::updateCategory by id {} response {}", id, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteCategory(@PathVariable("id") Integer id) {
        log.info("CategoryController::deleteCategory by id {}", id);
        CategoryDto categoryDto = categoryService.deleteCategory(id);
        Response<Object> response = Response.ok().setPayload(categoryDto);
        log.info("CategoryController::deleteCategory by id {} response {}", id, ValueMapper.jsonAsString(response));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
