package com.xoraus.cyberscribex.service.impl;

import com.xoraus.cyberscribex.entity.Category;
import com.xoraus.cyberscribex.entity.Post;
import com.xoraus.cyberscribex.exception.ResourceNotFoundException;
import com.xoraus.cyberscribex.mapper.PostMapper;
import com.xoraus.cyberscribex.payload.PostDto;
import com.xoraus.cyberscribex.payload.PostResponse;
import com.xoraus.cyberscribex.repository.CategoryRepository;
import com.xoraus.cyberscribex.repository.PostRepository;
import com.xoraus.cyberscribex.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PostServiceImp implements PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final PostMapper postMapper;

    public PostServiceImp(PostRepository postRepository,
                          CategoryRepository categoryRepository,
                          PostMapper postMapper) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.postMapper = postMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"posts", "postSearch"}, allEntries = true)
    public PostDto createPost(PostDto postDto) {
        Category category = categoryRepository.findActiveById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        Post post = postMapper.toEntity(postDto);
        post.setCategory(category);
        Post savedPost = postRepository.save(post);

        log.info("Post created with id: {}, title: {}", savedPost.getId(), savedPost.getTitle());
        return postMapper.toDto(savedPost);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "posts", key = "#pageNo + '-' + #pageSize + '-' + #sortBy + '-' + #sortDir")
    public PostResponse getAllPosts(int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<Post> posts = postRepository.findAllActive(pageable);
        List<Post> listOfPosts = posts.getContent();

        List<PostDto> content = postMapper.toDtoList(listOfPosts);

        return PostResponse.builder()
                .content(content)
                .pageNo(posts.getNumber())
                .pageSize(posts.getSize())
                .totalElements(posts.getTotalElements())
                .totalPages(posts.getTotalPages())
                .last(posts.isLast())
                .first(posts.isFirst())
                .hasNext(posts.hasNext())
                .hasPrevious(posts.hasPrevious())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "posts", key = "#id")
    public PostDto getPostById(Long id) {
        Post post = postRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return postMapper.toDto(post);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"posts", "postSearch"}, allEntries = true)
    public PostDto updatePost(PostDto postDto, Long id) {
        Post post = postRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        Category category = categoryRepository.findActiveById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());
        post.setCategory(category);

        Post updatedPost = postRepository.save(post);
        log.info("Post updated with id: {}", updatedPost.getId());
        return postMapper.toDto(updatedPost);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"posts", "postSearch"}, allEntries = true)
    public void deletePostById(Long id) {
        Post post = postRepository.findActiveById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        postRepository.delete(post);
        log.info("Post deleted with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> getPostsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }
        List<Post> posts = postRepository.findByCategoryId(categoryId);
        return postMapper.toDtoList(posts);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "postSearch", key = "#query")
    public List<PostDto> searchPosts(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query must not be empty");
        }
        List<Post> posts = postRepository.searchPosts(query.trim());
        return postMapper.toDtoList(posts);
    }
}
