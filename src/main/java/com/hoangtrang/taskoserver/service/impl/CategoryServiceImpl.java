package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.request.CategoryRequest;
import com.hoangtrang.taskoserver.dto.response.CategoryResponse;
import com.hoangtrang.taskoserver.exception.AppException;
import com.hoangtrang.taskoserver.exception.ErrorStatus;
import com.hoangtrang.taskoserver.mapper.CategoryMapper;
import com.hoangtrang.taskoserver.model.Category;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.repository.CategoryRepository;
import com.hoangtrang.taskoserver.repository.UserRepository;
import com.hoangtrang.taskoserver.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    UserRepository userRepository;
    CategoryMapper categoryMapper;


    @Override
    public CategoryResponse createCategory(CategoryRequest request, User user) {
        User foundUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new AppException(ErrorStatus.USER_NOT_EXISTED));

        Category category = categoryMapper.toCategory(request);
        category.setUser(foundUser);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryResponse(savedCategory);

    }

    @Override
    public List<CategoryResponse> getCategories(User user) {
        User foundUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new AppException(ErrorStatus.USER_NOT_EXISTED));

        return categoryRepository.findAllByUser(foundUser)
                 .stream().map(categoryMapper::toCategoryResponse)
                 .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(UUID id, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorStatus.CATEGORY_NOT_FOUND));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CategoryRequest request, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorStatus.CATEGORY_NOT_FOUND));
        category.setName(request.name());
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id, User user) {
        Category category = categoryRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new AppException(ErrorStatus.CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }
}
