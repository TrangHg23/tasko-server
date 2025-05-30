package com.hoangtrang.taskoserver.controller;

import com.hoangtrang.taskoserver.config.security.CustomUserDetails;
import com.hoangtrang.taskoserver.dto.category.CategoryRequest;
import com.hoangtrang.taskoserver.dto.category.CategoryResponse;
import com.hoangtrang.taskoserver.dto.common.ResponseData;
import com.hoangtrang.taskoserver.model.User;
import com.hoangtrang.taskoserver.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Category Controller")
@RequestMapping("/api/categories")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    CategoryService categoryService;

    @Operation(summary = "Create a new category")
    @PostMapping
    public ResponseData<CategoryResponse> createCategory(
            @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            ) {
        User user = customUserDetails.user();
        CategoryResponse response = categoryService.createCategory(request, user);
        return new ResponseData<CategoryResponse>(HttpStatus.CREATED.value(),
                "Created new category successfully", response);
    }


    @Operation(summary = "Get all categories")
    @GetMapping
    public ResponseData<List<CategoryResponse>> getCategories(@AuthenticationPrincipal CustomUserDetails user) {
        var result = categoryService.getCategories(user.user());
        return new ResponseData<>(HttpStatus.OK.value(), "Get categories list successfully",result);
    }

    @Operation(summary = "Get category by id")
    @GetMapping("/{id}")
    public ResponseData<CategoryResponse> getCategoryById(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        CategoryResponse result = categoryService.getCategoryById(id, user.user());
        return new ResponseData<>(HttpStatus.OK.value(), "Get category successfully", result);
    }


    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    public ResponseData<CategoryResponse> updateCategory(
            @PathVariable UUID id,
            @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        CategoryResponse response = categoryService.updateCategory(id, request, user.user());
        return new ResponseData<>(HttpStatus.OK.value(), "Updated category successfully", response);
    }


    @Operation(summary = "Delete category")
    @DeleteMapping("/{id}")
    public ResponseData<Void> deleteCategory(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        categoryService.deleteCategory(id, user.user());
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete category successfully");
    }


}
