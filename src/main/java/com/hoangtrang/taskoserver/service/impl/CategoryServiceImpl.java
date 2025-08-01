package com.hoangtrang.taskoserver.service.impl;

import com.hoangtrang.taskoserver.dto.category.CategoryRequest;
import com.hoangtrang.taskoserver.dto.category.CategoryResponse;
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
    public void createDefaultCategories(User currentUser) {
        List<String> defaultNames = List.of("Home", "Work");
        List<Category>  defaultCategories = defaultNames.stream()
                .map((name) -> Category.builder()
                        .name(name)
                        .user(currentUser)
                        .build())
                .toList();

        categoryRepository.saveAll(defaultCategories);
    }

    @Override
    public List<CategoryResponse> getCategories(User user) {
        User foundUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new AppException(ErrorStatus.USER_NOT_EXISTED));

        return categoryRepository.findAllCategories(foundUser.getId());
    }

    @Override
    public CategoryResponse getCategoryById(UUID id, UUID userId) {
        Category category = loadCategoryByUserId(id, userId);
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public CategoryResponse updateCategory(UUID id, CategoryRequest request, UUID userId) {
        Category category = loadCategoryByUserId(id, userId);

        category.setName(request.name());
        Category updatedCategory = categoryRepository.save(category);
        return categoryMapper.toCategoryResponse(updatedCategory);
    }

    @Override
    public void deleteCategory(UUID id, UUID userId) {
        Category category = loadCategoryByUserId(id, userId);
        categoryRepository.delete(category);
    }

    private Category loadCategoryByUserId(UUID id, UUID userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new AppException(ErrorStatus.CATEGORY_NOT_FOUND));
    }
}
