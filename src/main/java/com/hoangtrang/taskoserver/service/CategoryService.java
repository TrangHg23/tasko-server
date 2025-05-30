package com.hoangtrang.taskoserver.service;

import com.hoangtrang.taskoserver.dto.category.CategoryRequest;
import com.hoangtrang.taskoserver.dto.category.CategoryResponse;
import com.hoangtrang.taskoserver.model.User;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request, User user);

    List<CategoryResponse> getCategories(User user);

    CategoryResponse getCategoryById(UUID id, User user);

    CategoryResponse updateCategory(UUID id, CategoryRequest request, User user);

    void deleteCategory(UUID id, User user);

}
