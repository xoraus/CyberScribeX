package com.xoraus.cyberscribex.controller;

import com.xoraus.cyberscribex.payload.ApiResult;
import com.xoraus.cyberscribex.payload.CategoryDto;
import com.xoraus.cyberscribex.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "CRUD operations for blog categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
            summary = "Create Category",
            description = "Create a new blog category (ADMIN only)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Category created successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @ApiResponse(responseCode = "409", description = "Category already exists")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResult<CategoryDto>> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategory = categoryService.addCategory(categoryDto);
        return ResponseEntity.ok(ApiResult.success("Category created successfully", savedCategory));
    }

    @Operation(
            summary = "Get Category by ID",
            description = "Retrieve a single category by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Category retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<CategoryDto>> getCategory(@PathVariable("id") Long categoryId) {
        CategoryDto category = categoryService.getCategory(categoryId);
        return ResponseEntity.ok(ApiResult.success(category));
    }

    @Operation(
            summary = "Get All Categories",
            description = "Retrieve all blog categories"
    )
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @GetMapping
    public ResponseEntity<ApiResult<List<CategoryDto>>> getCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResult.success(categories));
    }

    @Operation(
            summary = "Update Category",
            description = "Update an existing category (ADMIN only)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResult<CategoryDto>> updateCategory(
            @PathVariable("id") Long categoryId,
            @Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto updatedCategory = categoryService.updateCategory(categoryDto, categoryId);
        return ResponseEntity.ok(ApiResult.success("Category updated successfully", updatedCategory));
    }

    @Operation(
            summary = "Delete Category",
            description = "Delete a category (ADMIN only)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Category deleted successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult<Void>> deleteCategory(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResult.success("Category deleted successfully", null));
    }
}
