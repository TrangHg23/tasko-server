package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.category.CategoryRequest;
import com.hoangtrang.taskoserver.dto.category.CategoryResponse;
import com.hoangtrang.taskoserver.model.User;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request, User user);

    List<CategoryResponse> getCategories(User user);

    CategoryResponse getCategoryById(UUID id, UUID userId);

    CategoryResponse updateCategory(UUID id, CategoryRequest request, UUID userId);

    void deleteCategory(UUID id, UUID userId);

    void createDefaultCategories(User user);

}
