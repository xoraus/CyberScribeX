package com.xoraus.cyberscribex.service.impl;

import com.xoraus.cyberscribex.entity.Category;
import com.xoraus.cyberscribex.exception.CyberScribeXAPIException;
import com.xoraus.cyberscribex.exception.ResourceNotFoundException;
import com.xoraus.cyberscribex.mapper.CategoryMapper;
import com.xoraus.cyberscribex.payload.CategoryDto;
import com.xoraus.cyberscribex.repository.CategoryRepository;
import com.xoraus.cyberscribex.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto addCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new CyberScribeXAPIException(HttpStatus.CONFLICT,
                    "Category with name '" + categoryDto.getName() + "' already exists");
        }

        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);

        log.info("Category created with id: {}, name: {}", savedCategory.getId(), savedCategory.getName());
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "#categoryId")
    public CategoryDto getCategory(Long categoryId) {
        Category category = categoryRepository.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAllActive();
        return categoryMapper.toDtoList(categories);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDto updateCategory(CategoryDto categoryDto, Long categoryId) {
        Category category = categoryRepository.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));

        if (!category.getName().equals(categoryDto.getName()) && categoryRepository.existsByName(categoryDto.getName())) {
            throw new CyberScribeXAPIException(HttpStatus.CONFLICT,
                    "Category with name '" + categoryDto.getName() + "' already exists");
        }

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated with id: {}", updatedCategory.getId());
        return categoryMapper.toDto(updatedCategory);
    }

    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findActiveById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        categoryRepository.delete(category);
        log.info("Category deleted with id: {}", categoryId);
    }
}
