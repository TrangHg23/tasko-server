package com.hoangtrang.taskoserver.mapper;

import com.hoangtrang.taskoserver.dto.request.CategoryRequest;
import com.hoangtrang.taskoserver.dto.response.CategoryResponse;
import com.hoangtrang.taskoserver.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequest request);
    CategoryResponse toCategoryResponse(Category category);
}
