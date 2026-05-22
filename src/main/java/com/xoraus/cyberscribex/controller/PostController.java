package com.xoraus.cyberscribex.controller;

import com.xoraus.cyberscribex.payload.ApiResult;
import com.xoraus.cyberscribex.payload.PostDto;
import com.xoraus.cyberscribex.payload.PostResponse;
import com.xoraus.cyberscribex.service.PostService;
import com.xoraus.cyberscribex.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@Tag(name = "Posts", description = "CRUD operations for blog posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @Operation(
            summary = "Create Post",
            description = "Create a new blog post (ADMIN only)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "201", description = "Post created successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResult<PostDto>> createPost(@Valid @RequestBody PostDto postDto) {
        PostDto createdPost = postService.createPost(postDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success("Post created successfully", createdPost));
    }

    @Operation(
            summary = "Get All Posts",
            description = "Retrieve paginated list of all blog posts"
    )
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    @GetMapping
    public ResponseEntity<ApiResult<PostResponse>> getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_ORDER, required = false) String sortDir) {

        PostResponse postResponse = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(ApiResult.success(postResponse));
    }

    @Operation(
            summary = "Get Post by ID",
            description = "Retrieve a single blog post by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Post retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResult<PostDto>> getPostById(@PathVariable Long id) {
        PostDto post = postService.getPostById(id);
        return ResponseEntity.ok(ApiResult.success(post));
    }

    @Operation(
            summary = "Update Post",
            description = "Update an existing blog post (ADMIN only)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Post updated successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @ApiResponse(responseCode = "404", description = "Post or category not found")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResult<PostDto>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostDto postDto) {
        PostDto updatedPost = postService.updatePost(postDto, id);
        return ResponseEntity.ok(ApiResult.success("Post updated successfully", updatedPost));
    }

    @Operation(
            summary = "Delete Post",
            description = "Delete a blog post (ADMIN only)",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponse(responseCode = "200", description = "Post deleted successfully")
    @ApiResponse(responseCode = "403", description = "Insufficient privileges")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult<Void>> deletePost(@PathVariable Long id) {
        postService.deletePostById(id);
        return ResponseEntity.ok(ApiResult.success("Post deleted successfully", null));
    }

    @Operation(
            summary = "Get Posts by Category",
            description = "Retrieve all posts belonging to a specific category"
    )
    @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @GetMapping("/category/{id}")
    public ResponseEntity<ApiResult<List<PostDto>>> getPostsByCategory(@PathVariable("id") Long categoryId) {
        List<PostDto> posts = postService.getPostsByCategory(categoryId);
        return ResponseEntity.ok(ApiResult.success(posts));
    }

    @Operation(
            summary = "Search Posts",
            description = "Search posts by title, description, or content"
    )
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    @GetMapping("/search")
    public ResponseEntity<ApiResult<List<PostDto>>> searchPosts(
            @RequestParam(value = "q") String query) {
        List<PostDto> posts = postService.searchPosts(query);
        return ResponseEntity.ok(ApiResult.success(posts));
    }
}
