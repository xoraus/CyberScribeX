package com.xoraus.cyberscribex.controller;

import com.xoraus.cyberscribex.payload.ApiResult;
import com.xoraus.cyberscribex.payload.CommentDto;
import com.xoraus.cyberscribex.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Comments", description = "CRUD operations for post comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(
            summary = "Create Comment",
            description = "Add a comment to a blog post"
    )
    @ApiResponse(responseCode = "201", description = "Comment created successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResult<CommentDto>> createComment(
            @PathVariable(value = "postId") Long postId,
            @Valid @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.createComment(postId, commentDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResult.success("Comment created successfully", createdComment));
    }

    @Operation(
            summary = "Get Comments by Post",
            description = "Retrieve all comments for a specific post"
    )
    @ApiResponse(responseCode = "200", description = "Comments retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Post not found")
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResult<List<CommentDto>>> getCommentsByPostId(
            @PathVariable(value = "postId") Long postId) {
        List<CommentDto> comments = commentService.getAllComments(postId);
        return ResponseEntity.ok(ApiResult.success(comments));
    }

    @Operation(
            summary = "Get Comment by ID",
            description = "Retrieve a specific comment by post ID and comment ID"
    )
    @ApiResponse(responseCode = "200", description = "Comment retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Post or comment not found")
    @GetMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<ApiResult<CommentDto>> getCommentById(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "id") Long id) {
        CommentDto comment = commentService.getCommentById(postId, id);
        return ResponseEntity.ok(ApiResult.success(comment));
    }

    @Operation(
            summary = "Update Comment",
            description = "Update an existing comment"
    )
    @ApiResponse(responseCode = "200", description = "Comment updated successfully")
    @ApiResponse(responseCode = "404", description = "Post or comment not found")
    @PutMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<ApiResult<CommentDto>> updateComment(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody CommentDto commentDto) {
        CommentDto updatedComment = commentService.updateComment(postId, id, commentDto);
        return ResponseEntity.ok(ApiResult.success("Comment updated successfully", updatedComment));
    }

    @Operation(
            summary = "Delete Comment",
            description = "Delete a comment from a post"
    )
    @ApiResponse(responseCode = "200", description = "Comment deleted successfully")
    @ApiResponse(responseCode = "404", description = "Post or comment not found")
    @DeleteMapping("/posts/{postId}/comments/{id}")
    public ResponseEntity<ApiResult<Void>> deleteComment(
            @PathVariable(value = "postId") Long postId,
            @PathVariable(value = "id") Long id) {
        commentService.deleteComment(postId, id);
        return ResponseEntity.ok(ApiResult.success("Comment deleted successfully", null));
    }
}
